package service;

import exceptions.ManagerSaveException;
import exceptions.TimePeriodsException;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Проверка обработки времени")
public class FixingTimeTest extends TaskManagerTest <InMemoryTaskManager>{
    InMemoryHistoryManager inMemoryHistoryManager;
    public static final LocalDateTime startTime = LocalDateTime.of(2024,5,20,12,4);
    @Override
    protected InMemoryTaskManager createTaskManager(){
        inMemoryHistoryManager = new InMemoryHistoryManager();
        return new InMemoryTaskManager(inMemoryHistoryManager);
    }
    @BeforeEach
    void init(){
        super.setUp();
    }

    @DisplayName("Задачи без указания даты, не учитваются в списке задач")
    @Test
    void TasksWithoutStartTimeNotCountInTreeSet() {
        Task task1 = new Task("Test_1", "testing",10,startTime);
        Task task2 = new Task("Test_2", "testing",20,startTime.plusHours(12));
        Task task3 = new Task("Test_3", "testing",30,startTime.minusMonths(3));
        Task task4 = new Task("Test_4", "testing");
        Task task5 = new Task("Test_5", "testing");

        TaskManager.createTask(task1);
        TaskManager.createTask(task2);
        TaskManager.createTask(task3);
        TaskManager.createTask(task4);
        TaskManager.createTask(task5);

        int tasksWithoutDefault = TaskManager.getPrioritizedTasks().size();
        int tasksWithDefault = TaskManager.getAllTasks().size();

        assertNotEquals(tasksWithoutDefault,tasksWithDefault);
    }

    @DisplayName("Задачи сортируются в порядке возрастания временной шкалы")
    @Test
    void TasksSortByTimelineInTreeSet() {
        Task task1 = new Task("Test_1", "testing",10,startTime);
        Task task2 = new Task("Test_1", "testing",10,startTime.minusMinutes(10));
        Task task3 = new Task("Test_1", "testing",10,startTime.minusMinutes(20));
        Task task4 = new Task("Test_1", "testing",10,startTime.minusMinutes(30));
        Task task5 = new Task("Test_1", "testing",10,startTime.minusMinutes(40));

        TaskManager.createTask(task1);
        TaskManager.createTask(task2);
        TaskManager.createTask(task3);
        TaskManager.createTask(task4);
        TaskManager.createTask(task5);

        List<Task> times = new ArrayList<>(TaskManager.getPrioritizedTasks());
        int iterator = TaskManager.getAllTasks().size();

        for (int i = 1; i <= iterator;i++) {
            LocalDateTime fromManager = TaskManager.getTasks().get(i).getStartTime();
            LocalDateTime fromSet = times.get(iterator-i).getStartTime();
            Assertions.assertEquals(fromManager,fromSet);
        }
    }


    @DisplayName("Длительность работы эпика равна сумме продолжительности всех его подзадач")
    @Test
    void epicDurationEqualsAllEpicSubTasksDuration(){
        Epic epic = new Epic("Epic","testing");
        SubTask subTask1 = new SubTask("Subtask1","testing",12);
        SubTask subTask2 = new SubTask("Subtask1","testing",24);
        SubTask subTask3 = new SubTask("Subtask1","testing",36);

        TaskManager.createEpic(epic);
        TaskManager.createSubTask(subTask1);
        TaskManager.createSubTask(subTask2);
        TaskManager.createSubTask(subTask3);

        Duration epicDuration = epic.getDuration();
        Duration subtasksDuration = subTask1.getDuration().plus(subTask2.getDuration()).plus(subTask3.getDuration());

        assertEquals(epicDuration,subtasksDuration);
    }

    @DisplayName("Проверка startTime и endTime работы эпика")
    @Test
    void checkStartAndEndTimeOfEpicWork(){
        Epic epic = new Epic("Epic","testing");
        SubTask subTask1 = new SubTask("Subtask1","testing",startTime.minusHours(10));
        SubTask subTask2 = new SubTask("Subtask1","testing",startTime.plusMinutes(25));
        SubTask subTask3 = new SubTask("Subtask1","testing",startTime.minusDays(1));
        SubTask subTask4 = new SubTask("Subtask1","testing",startTime.plusDays(3));

        TaskManager.createEpic(epic);
        TaskManager.createSubTask(subTask1);
        TaskManager.createSubTask(subTask2);
        TaskManager.createSubTask(subTask3);
        TaskManager.createSubTask(subTask4);

        LocalDateTime minTime = TaskManager.getAllSubTasksByEpic(epic).stream().map(Task::getStartTime).min(Comparator.naturalOrder()).orElse(Task.defaultTime);
        LocalDateTime maxTime = TaskManager.getAllSubTasksByEpic(epic).stream().max(Comparator.comparing(Task::getEndTime)).map(SubTask::getEndTime).orElse(Task.defaultTime);;
        //Обязательно ли использовать Optional для проерки на null или можно просто присваивать какое-то дефолтное значение в orElse?
        Assertions.assertEquals(minTime, epic.getStartTime());
        Assertions.assertEquals(maxTime, epic.getEpicEndTime());
    }

    @DisplayName("Задачи с пересекающимися значениями не попадают в список задач")
    @Test
    void tasksWithEqualsStartAndEndTimeNotAddedToList() {
        Task task1 = new Task("Test1","testing",10,startTime);
        Task task2 = new Task("Test2","testing",10,startTime);
        Task task3 = new Task("Test3","testing",15,startTime.plusMinutes(30));
        Task task4 = new Task("Test2","testing",30,startTime.plusMinutes(20));
        Task task5 = new Task("Test2","testing",40,startTime.plusHours(20));
        Task task6 = new Task("Test2","testing",20,startTime.plusHours(20));

        assertThrows(TimePeriodsException.class, () -> {TaskManager.createTask(task1);
            TaskManager.createTask(task2);
            TaskManager.createTask(task3);
            TaskManager.createTask(task4);
            TaskManager.createTask(task5);
            TaskManager.createTask(task6);}, "Интервалы задач не совпадают");
    }

    @DisplayName("Проверка исключений при попытке задать некорректное значение Duration")
    @Test
    void errorCheckTryingToSetAnIncorrectValueofDuration() {
        Task task1 = new Task("Test1","testing");
        assertThrows(DateTimeException.class, () -> {LocalDateTime min = LocalDateTime.MIN.minusMinutes(10);});
    }


}
