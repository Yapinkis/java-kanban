package service;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, SubTask> subTasks;
    private int seq = 0;
    private TreeSet<Task> tasksTreeSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    int generateId() {
        return ++seq;
    }

    protected HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public List<Task> getHistoryHManager() {
        return historyManager.getHistory();
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
        if (!checkValidation(task)) {
            throw new IllegalArgumentException("Не соотвествуют формату - Имя, описание задачи");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        historyManager.add(task);
        if (!task.getStartTime().equals(Task.defaultTime) && !checkWorkTime(task)) {
            tasksTreeSet.add(task);
        }
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (!checkValidation(task)) {
            throw new IllegalArgumentException("Не соотвествуют формату - Имя, описание");
        }
        tasksTreeSet.remove(task);
        tasks.computeIfPresent(task.getId(), (id, editTask) -> {
            editTask.setName(task.getName());
            editTask.setDescription(editTask.getDescription());
            editTask.setTasksStatus(editTask.getTasksStatus());
            return editTask;
        });
        if (!task.getStartTime().equals(Task.defaultTime) && !checkWorkTime(task)) {
            tasksTreeSet.add(task);
        }
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
        epics.values().forEach(epic -> epic.setTasksStatus(TasksStatus.NEW));
        epics.values().forEach(epic -> epic.getSubTasks().clear());
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
        if (!checkValidation(subTask)) {
            throw new IllegalArgumentException("Не соотвествуют формату - Имя, описание");
        }
        subTask.setId((generateId()));
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(findEpic());
        subTask.setEpic(epic);
        epic.addSubTasks(subTask);
        calculateEpicStatus(epic);
        calculateEpicTime(epic);
        historyManager.add(subTask);
        if (!subTask.getStartTime().equals(Task.defaultTime) && !checkWorkTime(subTask)) {
            tasksTreeSet.add(subTask);
        }
        return subTask;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
            if (!checkValidation(subTask)) {
                throw new IllegalArgumentException("Не соотвествуют формату - Имя, описание");
            }
            tasksTreeSet.remove(subTask);
            subTasks.computeIfPresent(subTask.getId(), (id,editSubTask) -> {
                editSubTask.setName(editSubTask.getName());
                editSubTask.setDescription(editSubTask.getDescription());
                editSubTask.setTasksStatus(editSubTask.getTasksStatus());
                return editSubTask;
            });
            Epic epic = subTask.getEpic();
            calculateEpicStatus(epic);
            calculateEpicTime(epic);
            if (!subTask.getStartTime().equals(Task.defaultTime) && !checkWorkTime(subTask)) {
                tasksTreeSet.add(subTask);
            }
        }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        subTasks.remove(id);
        Epic epic = subTask.getEpic();
        epic.getSubTasks().remove(subTask.getId());
        calculateEpicStatus(epic);
        calculateEpicTime(epic);
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
    public Task createEpic(Epic epic) {
        if (!checkValidation(epic)) {
            throw new IllegalArgumentException("Не соотвествуют формату - Имя, описание");
        }
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        historyManager.add(epic);
        if (!epic.getStartTime().equals(Task.defaultTime) && !checkWorkTime(epic)) {
            tasksTreeSet.add(epic);
        }
        return null;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!checkValidation(epic)) {
            throw new IllegalArgumentException("Не соотвествуют формату - Имя, описание");
        }
        tasksTreeSet.remove(epic);
        epics.computeIfPresent(epic.getId(),(id,editEpic) -> {
           editEpic.setSubTasks(getSubTasks(editEpic));
           editEpic.setName(editEpic.getName());
           editEpic.setDescription(editEpic.getDescription());
           return editEpic;
        });
        calculateEpicStatus(epic);
        calculateEpicTime(epic);
        if (!epic.getStartTime().equals(Task.defaultTime) && !checkWorkTime(epic)) {
            tasksTreeSet.add(epic);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        epic.getSubTasks().clear();
        epic.getSubTasks().clear();
        epics.remove(id);
    }

    @Override
    public List<SubTask> showEpicSubtasks(Epic epic) {
        return getSubTasks(epic);
    }

    @Override
    public int findEpic() {
        return epics.values().stream().mapToInt(Task::getId).max().orElse(0);
    }

    @Override
    public void calculateEpicStatus(Epic epic) {
        List<SubTask> epicListStatus = getSubTasks(epic);
        if (epicListStatus.isEmpty()) {
            epic.setTasksStatus(TasksStatus.NEW);
        }
        boolean matchNew = epicListStatus.stream().allMatch(subFound -> subFound.getTasksStatus() == TasksStatus.NEW);
        boolean matchDone = epicListStatus.stream().allMatch(subFound -> subFound.getTasksStatus() == TasksStatus.DONE);
        if (matchNew) {
            epic.setTasksStatus(TasksStatus.NEW);
        } else if (matchDone) {
            epic.setTasksStatus(TasksStatus.DONE);
        } else {
            epic.setTasksStatus(TasksStatus.IN_PROGRESS);
        }
    }

    @Override
    public void calculateEpicTime(Epic epic) {
        List<SubTask> epicListTime = getSubTasks(epic);
        Optional<SubTask> leastTime = epicListTime.stream().filter(minTime -> !minTime.getStartTime()
                .equals(Task.defaultTime))
                .min(Comparator.comparing(Task::getStartTime));
        if (leastTime.isPresent()) {
            SubTask minTime = leastTime.get();
            epic.setStartTime(minTime);
        } else {
            System.out.println("SubTask с значением StartTime отсутствует в списке");
        }
        Optional<SubTask> longestTime = epicListTime.stream().filter(maxTime -> !maxTime.getEndTime()
                .equals(Task.defaultTime)).max(Comparator.comparing(Task::getEndTime));
        if (longestTime.isPresent()) {
            SubTask maxTime = longestTime.get();
            epic.setEndTime(maxTime);
        } else {
            System.out.println("SubTask с значением EndTime отсутствует в списке");
        }
        epic.setDuration(epicListTime.stream().map(SubTask::getDuration).reduce(Duration.ZERO, (a,b) -> a.plus(b)).toMinutes());
    }

    //Набор вспомогательных методов
    @Override
    public boolean checkValidation(Task task) {
        if (task.getName() == null && task.getDescription() == null) {
            System.out.println("Входные данные задачи: " + task.getId());
            return false;
        }
        return true;
    }

    @Override
    public List<SubTask> getAllSubTasksByEpic(Epic epic) {
        return getSubTasks(epic);
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(tasksTreeSet);
    }

    @Override
    public boolean checkWorkTime(Task task) {
        boolean result;
        if (task.getTasksType().equals(TasksType.EPIC)) {
            Epic epic = epics.get(task.getId());
            LocalDateTime x1 = epic.getStartTime();
            LocalDateTime x2 = epic.getEpicEndTime();
            result = resultCalc(x1,x2);
        } else {
            LocalDateTime x1 = task.getStartTime();
            LocalDateTime x2 = task.getEndTime();
            result = resultCalc(x1,x2);
        }
        return result;
    }

    public boolean resultCalc(LocalDateTime x1, LocalDateTime x2) {
        return getPrioritizedTasks().stream().anyMatch(x -> x.getStartTime().isBefore(x2) && x.getEndTime().isAfter(x1));
    }

    public List<SubTask> getSubTasks(Epic epic) {
        List<SubTask> arr = new ArrayList<>();
        for (Integer key : epic.getSubTasks()) {
            if (subTasks.containsKey(key)) {
                arr.add(subTasks.get(key));
            }
        }
        return arr;
    }
}
