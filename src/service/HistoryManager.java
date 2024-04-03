package service;

import model.Node;
import model.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(Node node);

    List<Task> getHistory();
}
