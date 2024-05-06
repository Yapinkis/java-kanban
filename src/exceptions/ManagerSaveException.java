package exceptions;
//Я же правильно понял, что для исключений должна быть отдельная директория?
public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(String message, Exception exception) {
        super(message, exception);
    }
    //Допустимо ли принимать в конструктор исключение от Exception, учитывая что ManagerSaveException
    //наследуется от RuntimeException. Я понимаю, что все исключения наследуются от Exception, но тем не менее
}

