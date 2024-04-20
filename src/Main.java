import model.Task;
import service.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        TaskManager inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManager.createTask(new Task("One","Split"));
        inMemoryTaskManager.createTask(new Task("1ne","Split"));
        inMemoryTaskManager.createTask(new Task("2ne","Split"));
        inMemoryTaskManager.createTask(new Task("3ne","Split"));
        inMemoryTaskManager.createTask(new Task("4ne","Split"));
        inMemoryTaskManager.getIdTask(2);
        inMemoryTaskManager.getIdTask(1);
        inMemoryTaskManager.getIdTask(3);
        inMemoryTaskManager.getIdTask(2);
        List<Task> historyManager = inMemoryTaskManager.getHistoryManager();
        System.out.println(historyManager);
    }
}
