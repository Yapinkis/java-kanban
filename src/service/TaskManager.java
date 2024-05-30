package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    List<Task> getAllTasks();

    void clearAllTasks();

    Task getIdTask(int id);

    Task createTask(Task task);

    Set<Task> getPrioritizedTasks();//Или лучше явно указать TreeSet?

    boolean checkValidation(Task task);

    boolean checkWorkTime(Task task);

    void updateTask(Task task);

    void deleteTask(int id);

    List<SubTask> getAllSubTasks();

    void clearAllSubTasks();

    SubTask getIdSubtasks(int id);

    SubTask createSubTask(SubTask subTask);

    void updateSubTask(SubTask subTask);

    void deleteSubTask(int id);

    List<Epic> getAllEpics();

    void clearAllEpics();

    Epic getIdEpic(int id);

    Task createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    List<SubTask> showEpicSubtasks(Epic epic);

    int findEpic();

    void calculateEpicStatus(Epic epic);

    void calculateEpicTime(Epic epic);

    List<SubTask> getAllSubTasksByEpic(Epic epic);

}
