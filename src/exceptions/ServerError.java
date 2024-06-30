package exceptions;

public class ServerError extends BaseException {
    public ServerError(String message) {
        super(message);
    }

    public ServerError(String message, Exception exception) {
        super(message, exception);
    }
}
