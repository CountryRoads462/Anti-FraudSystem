package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

@RestController
public class TransactionController {

    @Autowired
    SuspiciousIPRepository suspiciousIPRepo;

    @Autowired
    StolenCardRepository stolenCardRepo;

    @Autowired
    TransactionRepository transactionRepo;

    @Autowired
    AmountLimitRepository amountLimitRepo;

    @PostMapping("/api/antifraud/transaction")
    public LinkedHashMap<String, String> validateTransaction(@Valid @RequestBody(required = false) Transaction transaction) {

        String regionCode = transaction.getRegion();
        if (!ValidRegionCodeChecker.check(regionCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        transactionRepo.save(transaction);

        TreeSet<String> rejectingReasons = new TreeSet<>();

        TransactionValidationResult validationResult = TransactionValidationResult.ALLOWED;

        List<Transaction> transactionList = new ArrayList<>();

        Set<String> ipSet = new HashSet<>();
        Set<String> regionSet = new HashSet<>();
        transactionRepo.findAll().forEach(transactionList::add);
        transactionList.stream()
                .filter(transaction1 -> Objects.equals(transaction1.getNumber(), transaction.getNumber()))
                .filter(transaction1 -> (transaction.getDate().minusHours(1).isBefore(transaction1.getDate()) ||
                        transaction.getDate().minusHours(1).isEqual(transaction1.getDate())) &&
                        (transaction1.getDate().isBefore(transaction.getDate()) ||
                                transaction.getDate().isEqual(transaction1.getDate())))
                .forEach(transaction1 -> {
                    ipSet.add(transaction1.getIp());
                    regionSet.add(transaction1.getRegion());
                });
        int ipCorrelationIndex = ipSet.size();
        int regionCorrelationIndex = regionSet.size();

        if (ipCorrelationIndex == 3) {
            rejectingReasons.add("ip-correlation");
            validationResult = TransactionValidationResult.setValue(
                    validationResult,
                    TransactionValidationResult.MANUAL_PROCESSING
            );
        } else if (ipCorrelationIndex > 3) {
            rejectingReasons.add("ip-correlation");
            validationResult = TransactionValidationResult.setValue(
                    validationResult,
                    TransactionValidationResult.PROHIBITED
            );
        }

        if (regionCorrelationIndex == 3) {
            rejectingReasons.add("region-correlation");
            validationResult = TransactionValidationResult.setValue(
                    validationResult,
                    TransactionValidationResult.MANUAL_PROCESSING
            );
        } else if (regionCorrelationIndex > 3) {
            rejectingReasons.add("region-correlation");
            validationResult = TransactionValidationResult.setValue(
                    validationResult,
                    TransactionValidationResult.PROHIBITED
            );
        }

        long value = transaction.getAmount();

        long maxAllowed = amountLimitRepo.findById((long) 1).get().getMaxAllowed();
        long maxManual = amountLimitRepo.findById((long) 1).get().getMaxManual();

        if (value > maxAllowed && value <= maxManual) {
            validationResult = TransactionValidationResult.setValue(
                    validationResult,
                    TransactionValidationResult.MANUAL_PROCESSING
            );
            rejectingReasons.add("amount");
        }

        String numberOfCard = transaction.getNumber();
        if (!ValidCardChecker.check(numberOfCard) || stolenCardRepo.existsByNumber(numberOfCard)) {
            validationResult = TransactionValidationResult.setValue(
                    validationResult,
                    TransactionValidationResult.PROHIBITED
            );
            rejectingReasons.add("card-number");
            rejectingReasons.remove("amount");
        }

        String ipAddress = transaction.getIp();
        if (!ValidIPAddressChecker.check(ipAddress) || suspiciousIPRepo.existsByIp(ipAddress)) {
            validationResult = TransactionValidationResult.setValue(
                    validationResult,
                    TransactionValidationResult.PROHIBITED
            );
            rejectingReasons.add("ip");
            rejectingReasons.remove("amount");
        }

        if (value > maxManual) {
            validationResult = TransactionValidationResult.setValue(
                    validationResult,
                    TransactionValidationResult.PROHIBITED
            );
            rejectingReasons.add("amount");
        }

        LinkedHashMap<String, String> validateTransactionResponseForm = new LinkedHashMap<>();
        validateTransactionResponseForm.put("result", validationResult.name());
        if (validationResult == TransactionValidationResult.ALLOWED) {
            validateTransactionResponseForm.put("info", "none");
        } else {
            validateTransactionResponseForm.put("info",
                    String.valueOf(rejectingReasons).substring(1, String.valueOf(rejectingReasons).length() - 1));
        }

        //for debug

        try (FileWriter fileWriter = new FileWriter(
                "C:\\Users\\BotMachine\\OneDrive\\Рабочий стол\\Untitled.txt",
                true)
        ) {
            fileWriter.write("amount: " + transaction.getAmount() + "\n");
            fileWriter.write("ip: " + transaction.getIp() + "\n");
            fileWriter.write("number: " + transaction.getNumber() + "\n");
            fileWriter.write("region: " + transaction.getRegion() + "\n");
            fileWriter.write("date: " + transaction.getDate() + "\n");
            fileWriter.write("\n");
            fileWriter.write("result: " + validateTransactionResponseForm.get("result") + "\n");
            fileWriter.write("info: " + validateTransactionResponseForm.get("info") + "\n");
            fileWriter.write("\n");
            fileWriter.write("ipCorrelationIndex: " + ipCorrelationIndex + "\n");
            fileWriter.write("regionCorrelationIndex: " + regionCorrelationIndex + "\n");
            fileWriter.write("\n");
            fileWriter.write("ipAddresses: " + ipSet + "\n");
            fileWriter.write("regions: " + regionSet + "\n");
            fileWriter.write("\n");
            fileWriter.write("maxAllowed: " + maxAllowed + "\n");
            fileWriter.write("maxManual: " + maxManual + "\n");
            fileWriter.write("\n\n");
        } catch (IOException ignored) {
        }


        transaction.setResult(validationResult.name());
        transactionRepo.save(transaction);

        return validateTransactionResponseForm;
    }

    @PutMapping(path = "/api/antifraud/transaction")
    public Transaction addFeedback(@RequestBody AddFeedbackRequestForm addFeedbackRequestForm) throws Throwable {

        String feedbackRequest = addFeedbackRequestForm.getFeedback();

        if (!feedbackRequest.matches("(" +
                "ALLOWED|" +
                "MANUAL_PROCESSING|" +
                "PROHIBITED" +
                ")")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = transactionRepo.findById(addFeedbackRequestForm.getTransactionId())
                .orElseThrow((Supplier<Throwable>) () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String transactionResult = transaction.getResult();

        if (!transaction.getFeedback().equals("")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (transactionResult.equals(feedbackRequest)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        AmountLimit amountLimit = amountLimitRepo.findById( (long) 1).orElse(new AmountLimit(1));

        long transactionAmount = transaction.getAmount();

        if (transactionResult.equals("ALLOWED") && feedbackRequest.equals("MANUAL_PROCESSING")) {
            amountLimit.decreaseMaxAllowed(transactionAmount);
        } else if (transactionResult.equals("ALLOWED") && feedbackRequest.equals("PROHIBITED")) {
            amountLimit.decreaseMaxAllowed(transactionAmount);
            amountLimit.decreaseMaxManual(transactionAmount);
        } else if (transactionResult.equals("MANUAL_PROCESSING") && feedbackRequest.equals("ALLOWED")) {
            amountLimit.increaseMaxAllowed(transactionAmount);
        } else if (transactionResult.equals("MANUAL_PROCESSING") && feedbackRequest.equals("PROHIBITED")) {
            amountLimit.decreaseMaxManual(transactionAmount);
        } else if (transactionResult.equals("PROHIBITED") && feedbackRequest.equals("ALLOWED")) {
            amountLimit.increaseMaxAllowed(transactionAmount);
            amountLimit.increaseMaxManual(transactionAmount);
        } else {
            amountLimit.increaseMaxManual(transactionAmount);
        }

        transaction.setFeedback(feedbackRequest);
        transactionRepo.save(transaction);

        amountLimitRepo.save(amountLimit);
        return transaction;
    }

    @GetMapping(path = "/api/antifraud/history")
    public List<Transaction> getTransactionsHistory() {

        List<Transaction> transactionList = new ArrayList<>();
        transactionRepo.findAll().forEach(transactionList::add);

        return transactionList;
    }


    @GetMapping(path = "/api/antifraud/history/{number}")
    public List<Transaction> getTransactionsByNumber(@PathVariable String number) {

        if (!ValidCardChecker.check(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        List<Transaction> transactionList = transactionRepo.findTransactionByNumber(number);
        if (transactionList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return transactionList;
    }
}
