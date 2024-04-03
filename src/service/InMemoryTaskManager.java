package service;

import model.EnumStatus;
import model.Epic;
import model.SubTask;
import model.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    Map<Integer, Task> tasks;//Я так понимаю, что такой подход позволит нам сделать любую реализацию
    //Map в конструкторе, как LinkedHashMap или TreeMap допустим
    Map<Integer, Epic> epics;
    Map<Integer, SubTask> subTasks;
    private int seq = 0;

    int generateId() {
        return ++seq;
    }

    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    //Набор метододов для задач Tasks
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
        System.out.println("Список задач очищен...");
    }

    @Override
    public Task getIdTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
            Task saved = tasks.get(task.getId());
            saved.setName(task.getName());
            saved.setDescription(task.getDescription());
            saved.setStatus(task.getStatus());
            tasks.put(task.getId(), task);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    //Набор метододов для подзадач SubTasks
    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void clearAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(EnumStatus.NEW);
        }
    }

    @Override
    public SubTask getIdSubtasks(int id) {
        if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
        }
        return subTasks.get(id);
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask.setId((generateId()));
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(findEpic());
        subTask.setEpic(epic);
        epic.addSubTasks(subTask);
        calculateEpic(epic);
        return subTask;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
            SubTask saved = subTasks.get(subTask.getId());
            saved.setName(subTask.getName());
            saved.setDescription(subTask.getDescription());
            saved.setStatus(subTask.getStatus());
            subTasks.put(subTask.getId(), subTask);
            Epic epic = subTask.getEpic();
            calculateEpic(epic);
        }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        subTasks.remove(id);
        Epic epic = subTask.getEpic();
        epic.getSubTasks().remove(subTask);
        calculateEpic(epic);
    }


    //Набор метододов для Эпиков Epics
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getIdEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        epic.setSubTasks(saved.getSubTasks());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        calculateEpic(epic);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        epic.getSubTasks().clear();
        epics.remove(id);
    }

    @Override
    public List<SubTask> showEpicSubtasks(Epic epic) {
        return epic.getSubTasks();
    }

    @Override
    public int findEpic() {
        int value = 0;
        for (Epic key: epics.values()) {
            if (key.getId() > value) {
                value = key.getId();
            }
        }
        return value;
    }

    @Override
    public void calculateEpic(Epic epic) {
        List<SubTask> epicListStatus = epic.getSubTasks();
        if (epicListStatus.isEmpty()) {
            epic.setStatus(EnumStatus.NEW);
        }
        boolean matchNew = epicListStatus.stream().allMatch(subFound -> subFound.getStatus() == EnumStatus.NEW);
        boolean matchDone = epicListStatus.stream().allMatch(subFound -> subFound.getStatus() == EnumStatus.DONE);
        if (matchNew) {
            epic.setStatus(EnumStatus.NEW);
        } else if (matchDone) {
            epic.setStatus(EnumStatus.DONE);
        } else {
            epic.setStatus(EnumStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<SubTask> getAllSubTasksByEpic(Epic epic) {
        List<SubTask> subList = epic.getSubTasks();
        return subList;
    }
}
