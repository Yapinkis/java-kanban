package exceptions;

public class TimePeriodsException extends BaseException {
    public TimePeriodsException(String message) {
        super(message);
    }

    public TimePeriodsException(String message, Exception exception) {
        super(message, exception);
    }
}
