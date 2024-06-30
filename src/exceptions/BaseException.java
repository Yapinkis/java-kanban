package exceptions;

public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable exception) {
        super(message, exception);
    }
}

