package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    //Набор метододов для задач Tasks
    List<Task> getAllTasks();

    void clearAllTasks();

    Task getIdTask(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    void deleteTask(int id);

    //Набор метододов для подзадач SubTasks
    List<SubTask> getAllSubTasks();

    void clearAllSubTasks();

    SubTask getIdSubtasks(int id);

    SubTask createSubTask(SubTask subTask);

    void updateSubTask(SubTask subTask);

    void deleteSubTask(int id);

    //Набор метододов для Эпиков Epics
    List<Epic> getAllEpics();

    void clearAllEpics();

    Epic getIdEpic(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    List<SubTask> showEpicSubtasks(Epic epic);

    int findEpic();

    void calculateEpic(Epic epic);

    List<SubTask> getAllSubTasksByEpic(Epic epic);

    List<Task> getHistoryManager();
}
