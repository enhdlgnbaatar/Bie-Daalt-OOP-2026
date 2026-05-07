// Энэ class нь үйлчилгээний method-үүдийн хариуг нэг бүтэцтэй болгоно.
public class OperationResult {
    private boolean success;
    private String message;
    private Account account;

    public OperationResult(boolean success, String message) {
        this(success, message, null);
    }

    public OperationResult(boolean success, String message, Account account) {
        this.success = success;
        this.message = message;
        this.account = account;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Account getAccount() {
        return account;
    }
}
