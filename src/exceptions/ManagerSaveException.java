package exceptions;

public class ManagerSaveException extends BaseException {
    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(String message, Exception exception) {
        super(message, exception);
    }
}
