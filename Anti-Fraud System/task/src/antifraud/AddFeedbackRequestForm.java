package antifraud;

public class AddFeedbackRequestForm {

    private long transactionId;
    private String feedback;

    public AddFeedbackRequestForm() {
    }

    public AddFeedbackRequestForm(long transactionId, String feedback) {
        this.transactionId = transactionId;
        this.feedback = feedback;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
