package antifraud;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AmountLimit {

    @Id
    private long id;

    private long maxAllowed = 200;

    private long maxManual = 1500;

    public AmountLimit(long id) {
        this.id = id;
    }

    public AmountLimit() {
    }

    public static long increaseFormula(Long currentLimit, Long valueFromTransaction) {
        double newLimit = 0.8 * currentLimit + 0.2 * valueFromTransaction;
        return (long) Math.ceil(newLimit);
    }

    public static long decreaseFormula(Long currentLimit, Long valueFromTransaction) {
        double newLimit = 0.8 * currentLimit - 0.2 * valueFromTransaction;
        return (long) Math.ceil(newLimit);
    }

    public void increaseMaxAllowed(Long valueFromTransaction) {
        maxAllowed = increaseFormula(maxAllowed, valueFromTransaction);
    }

    public void decreaseMaxAllowed(Long valueFromTransaction) {
        maxAllowed = decreaseFormula(maxAllowed, valueFromTransaction);
    }

    public void increaseMaxManual(Long valueFromTransaction) {
        maxManual = increaseFormula(maxManual, valueFromTransaction);
    }

    public void decreaseMaxManual(Long valueFromTransaction) {
        maxManual = decreaseFormula(maxManual, valueFromTransaction);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMaxAllowed() {
        return maxAllowed;
    }

    public void setMaxAllowed(long maxAllowed) {
        this.maxAllowed = maxAllowed;
    }

    public long getMaxManual() {
        return maxManual;
    }

    public void setMaxManual(long maxManual) {
        this.maxManual = maxManual;
    }
}
