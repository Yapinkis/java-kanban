package model;

import java.io.IOException;

public class ManagerSaveException extends IOException {
    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(String message, IOException exception) {
        super(message, exception);
    }
    //Или здесь в качестве аргумента нужно передавать Throwable, а не IOException exception
}
