package antifraud;

public enum TransactionValidationResult {
    ALLOWED(0),
    MANUAL_PROCESSING(1),
    PROHIBITED(2);

    private final int weight;

    TransactionValidationResult(int weight) {
        this.weight = weight;
    }

    static TransactionValidationResult setValue(TransactionValidationResult toChange, TransactionValidationResult value) {
        if (value.weight > toChange.weight) {
            return value;
        } else {
            return toChange;
        }
    }
}
