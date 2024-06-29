package service;

import model.TasksStatus;
import model.Epic;
import model.SubTask;
import model.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Работа: In Memory Task Manager Test")
class InMemoryTaskManagerTest extends TaskManagerTest <InMemoryTaskManager>{
    InMemoryHistoryManager inMemoryHistoryManager;

    @Override
    protected InMemoryTaskManager createTaskManager() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
        return new InMemoryTaskManager(inMemoryHistoryManager);
    }
    @BeforeEach
    void init() {
        super.setUp();
    }

    @DisplayName("Добавляет задачи разного типа")
    @Test
    void canAddTasksAndHeirsAndCanFindThemById() {

        Epic epic = new Epic("Epic", "Test");
        SubTask subTask = new SubTask("SubTask", "Test");
        Task task = new Task("Task", "Test");

        TaskManager.createEpic(epic);
        TaskManager.createSubTask(subTask);
        TaskManager.createTask(task);

        assertNotNull(TaskManager.getIdEpic(epic.getId()));
        assertNotNull(TaskManager.getIdSubtasks(subTask.getId()));
        assertNotNull(TaskManager.getIdTask(task.getId()));
    }
    @DisplayName("Их поиск по Id")
    @Test
    void canFindById() {

        Epic epic = new Epic("Epic", "Test");
        SubTask subTask = new SubTask("SubTask", "Test");
        Task task = new Task("Task", "Test");

        TaskManager.createEpic(epic);
        TaskManager.createSubTask(subTask);
        TaskManager.createTask(task);

        Epic foundEpic = TaskManager.getIdEpic(epic.getId());
        SubTask foundSubTask = TaskManager.getIdSubtasks(subTask.getId());
        Task foundTask = TaskManager.getIdTask(task.getId());

        assertEquals(epic, foundEpic);
        assertEquals(subTask, foundSubTask);
        assertEquals(task, foundTask);
    }
    @DisplayName("Проверка конфликта сгенерированного и заданного Id")
    @Test
    void compareGenerateIdAndSetId() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Random random = new Random();

        Task task_1 = new Task("Task_1", "Test generate id");
        Task task_2 = new Task("Task_2", "Test set id");

        inMemoryTaskManager.createTask(task_1);
        inMemoryTaskManager.createTask(task_2);

        task_2.setId(inMemoryTaskManager.generateId() + random.nextInt(inMemoryTaskManager.getAllTasks().size()));
        assertNotEquals(task_1.getId(), task_2.getId());
    }
    @DisplayName("Неизменность задач по полям при добавлении в Manager")
    @Test
    void cantFixCreatedPropertys() {
        Task task = new Task("Тестовая задача","Назначение");

        Task taskCopy = new Task(task.getName(),task.getDescription());
        taskCopy.setId(task.getId());
        taskCopy.setTasksStatus(task.getTasksStatus());
        taskCopy.setDescription(task.getDescription());

        TaskManager.createTask(task);

        assertEquals(taskCopy.getId(), task.getId());
        assertEquals(taskCopy.getName(), task.getName());
        assertEquals(taskCopy.getDescription(), task.getDescription());
        assertEquals(taskCopy.getTasksStatus(), task.getTasksStatus());
    }
    @Test
    @DisplayName("Проверка калькуляции статуса Эпика")
    void showCalculateEpicStatus() {
        Epic epic = new Epic("Test epic", "test");
        SubTask subTask1 = new SubTask("Test subTask1", "test_subTask1");
        SubTask subTask2 = new SubTask("Test subTask2", "test_subTask2");
        SubTask subTask3 = new SubTask("Test subTask3", "test_subTask3");

        TaskManager.createEpic(epic);
        TaskManager.createSubTask(subTask1);
        TaskManager.createSubTask(subTask2);
        TaskManager.createSubTask(subTask3);

        assertEquals(TasksStatus.NEW, epic.getTasksStatus());

        subTask1.setTasksStatus(TasksStatus.DONE);

        TaskManager.calculateEpicStatus(epic);

        assertEquals(TasksStatus.IN_PROGRESS, epic.getTasksStatus());

        subTask1.setTasksStatus(TasksStatus.DONE);
        subTask2.setTasksStatus(TasksStatus.DONE);
        subTask3.setTasksStatus(TasksStatus.DONE);

        TaskManager.calculateEpicStatus(epic);

        assertEquals(TasksStatus.DONE, epic.getTasksStatus());
    }
    @Test
    @DisplayName("Проверка удаления Эпика и его Сабтасков")
    void showDeleteEpicAndItsSubTasks(){
        Epic epic1 = new Epic("Test epic1", "test1");
        SubTask subTask1 = new SubTask("Test subTask1", "test_subTask1");
        SubTask subTask2 = new SubTask("Test subTask2", "test_subTask2");

        TaskManager.createEpic(epic1);
        TaskManager.createSubTask(subTask1);
        TaskManager.createSubTask(subTask2);

        Epic epic2 = new Epic("Test epic2", "test2");
        SubTask subTask3 = new SubTask("Test subTask3", "test_subTask3");
        SubTask subTask4 = new SubTask("Test subTask4", "test_subTask4");

        TaskManager.createEpic(epic2);
        TaskManager.createSubTask(subTask3);
        TaskManager.createSubTask(subTask4);

        TaskManager.deleteEpic(epic1.getId());

        int epicsSize = TaskManager.getAllEpics().size();

        assertNotEquals(epic1.getSubTasks().size(),epic2.getSubTasks().size());
        assertEquals(1,epicsSize);
    }
    @Test
    @DisplayName("Проверка обновления эпиков и его сабтасков")
    void showUpdateEpicAndIstSubTasks(){
        Epic epic = new Epic("Test epic1", "test1");
        SubTask subTask1 = new SubTask("Test subTask1", "test_subTask1");
        SubTask subTask2 = new SubTask("Test subTask2", "test_subTask2");

        TaskManager.createEpic(epic);
        TaskManager.createSubTask(subTask1);
        TaskManager.createSubTask(subTask2);

        String nameEpic = epic.getName();
        String descriptionEpic = epic.getDescription();
        epic.setName("new name");
        epic.setDescription("new description");

        TaskManager.updateEpic(epic);

        assertNotEquals(nameEpic,epic.getName());
        assertNotEquals(descriptionEpic,epic.getDescription());
    }

}