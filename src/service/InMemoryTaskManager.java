package service;

import exceptions.ManagerSaveException;
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

    //Набор метододов для задач Tasks
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearAllTasks() {
        getAllTasks().forEach(historyManager::remove);
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
        update(task);
        if (!task.getStartTime().equals(Task.defaultTime) && !checkWorkTime(task)) {
            tasksTreeSet.add(task);
        }
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(tasks.get(id));
        //Кажется я немного запутался...У нас по условию ТЗ6 нужно было удалять из истории при удалении задачи:
        /* Добавьте вызов метода при удалении задач, чтобы они удалялись также из истории просмотров.*/
        tasks.remove(id);
    }

    //Набор метододов для подзадач SubTasks
    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void clearAllSubTasks() {
        getAllSubTasks().forEach(historyManager::remove);
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
        subTask.setEpic(epic.getId());
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
            update(subTask);
            Epic epic = epics.get(subTask.getEpic());
            calculateEpicStatus(epic);
            calculateEpicTime(epic);
            if (!subTask.getStartTime().equals(Task.defaultTime) && !checkWorkTime(subTask)) {
                tasksTreeSet.add(subTask);
            }
        }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.remove(subTask);
        subTasks.remove(id);
        Epic epic = epics.get(subTask.getEpic());
        epic.getSubTasks().remove(subTask.getEpic());
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
        getAllEpics().forEach(historyManager::remove);
        getAllSubTasks().forEach(historyManager::remove);
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
        update(epic);
        calculateEpicStatus(epic);
        calculateEpicTime(epic);
        if (!epic.getStartTime().equals(Task.defaultTime) && !checkWorkTime(epic)) {
            tasksTreeSet.add(epic);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        getSubTasks(epic).forEach(historyManager::remove);
        epic.getSubTasks().clear();
        getSubTasks(epic).clear();
        historyManager.remove(epic);
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
    //а разве вариант с return tasksTreeSet не даёт открытый доступ к tasksTreeSet и это то, что я постоянно правил
    //У меня раньше было много замечаний по этому поводу, что я как раз возвращал оригинал. Или это ситуативная штука?
    //Если да,то как определить, когда давать прямой доступ, а когда передавать копию?

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

    @Override
    public List<Task> getHistoryHManager() {
        return historyManager.getHistory();
    }

    //Набор вспомогательных методов

    public boolean resultCalc(LocalDateTime x1, LocalDateTime x2) {
        boolean result;
        result = getPrioritizedTasks().stream().anyMatch(x -> x.getStartTime().isBefore(x2) && x.getEndTime().isAfter(x1));
        if (result) {
            throw new ManagerSaveException("Даты добавления задач совпадают");
        }
        return result;
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

    protected void calculateEpicStatus(Epic epic) {
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

    private void calculateEpicTime(Epic epic) {
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

    private void update(Task task) {
        switch (task.getTasksType()) {
            case TASK:
                tasks.computeIfPresent(task.getId(), (id, editTask) -> {
                    editTask.setName(task.getName());
                    editTask.setDescription(task.getDescription());
                    editTask.setTasksStatus(task.getTasksStatus());
                    return editTask;
                });
                break;
            case SUBTASK:
                subTasks.computeIfPresent(task.getId(), (id,editSubTask) -> {
                    editSubTask.setName(task.getName());
                    editSubTask.setDescription(task.getDescription());
                    editSubTask.setTasksStatus(task.getTasksStatus());
                    ((SubTask) editSubTask).setEpic(((SubTask) task).getEpic());
                    return editSubTask;
                });
                break;
            case EPIC:
                epics.computeIfPresent(task.getId(),(id,editEpic) -> {
                    ((Epic) editEpic).setSubTasks(((Epic) task).getSubTasks());
                    editEpic.setName(task.getName());
                    editEpic.setDescription(task.getDescription());
                    return editEpic;
                });
                break;
                //Или этот метод тоже нужно как-то разбить на подметоды?
        }
    }

}
