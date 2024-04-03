package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> history = new ArrayList<>();
    static final int maxCount = 9;
    @Override
    public void add(Task task) {
        if(history.size() > maxCount){
            history.remove(0);
            history.add(task);
        } else {
            history.add(task);
        }
    }
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
