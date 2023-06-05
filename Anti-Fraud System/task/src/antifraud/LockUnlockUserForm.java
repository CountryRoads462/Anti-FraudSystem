package antifraud;

public class LockUnlockUserForm {

    private String username;
    private String operation;

    public LockUnlockUserForm() {
    }

    public LockUnlockUserForm(String username, String operation) {
        this.username = username;
        this.operation = operation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
