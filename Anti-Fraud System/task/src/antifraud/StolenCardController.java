package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StolenCardController {

    @Autowired
    StolenCardRepository stolenCardRepo;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/api/antifraud/stolencard")
    public StolenCard addStolenCard(@RequestBody StolenCard stolenCard) {

        String number = stolenCard.getNumber();

        if (stolenCardRepo.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (!ValidCardChecker.check(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        stolenCardRepo.save(stolenCard);

        return stolenCard;
    }

    @Transactional
    @DeleteMapping(path = "/api/antifraud/stolencard/{number}")
    public Map<String, String> deleteStolenCard(@PathVariable String number) {

        if (!ValidCardChecker.check(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (!stolenCardRepo.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        stolenCardRepo.deleteByNumber(number);

        Map<String, String> deleteStolenCardResponseForm = new HashMap<>();

        deleteStolenCardResponseForm.put("status", String.format("Card %s successfully removed!", number));

        return deleteStolenCardResponseForm;
    }

    @GetMapping(path = "/api/antifraud/stolencard")
    public List<StolenCard> getStolenCards() {

        List<StolenCard> stolenCardList = new ArrayList<>();

        stolenCardRepo.findAll().forEach(stolenCardList::add);

        return stolenCardList;
    }
}
