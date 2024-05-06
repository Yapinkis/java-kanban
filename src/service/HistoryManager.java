package service;

import model.Task;
import service.InMemoryHistoryManager.Node;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(Node node);

    List<Task> getHistory();
}
