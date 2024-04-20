package service;

import model.EnumStatus;
import model.Epic;
import model.SubTask;
import model.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Работа: In Memory Task Manager Test")
class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
    }

    @DisplayName("Добавляет задачи разного типа")
    @Test
    void canAddTasksAndHeirsAndCanFindThemById() {

        Epic epic = new Epic("Epic", "Test");
        SubTask subTask = new SubTask("SubTask", "Test");
        Task task = new Task("Task", "Test");

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);
        taskManager.createTask(task);

        assertNotNull(taskManager.getIdEpic(epic.getId()));
        assertNotNull(taskManager.getIdSubtasks(subTask.getId()));
        assertNotNull(taskManager.getIdTask(task.getId()));
    }
    @DisplayName("Их поиск по Id")
    @Test
    void canFindById() {

        Epic epic = new Epic("Epic", "Test");
        SubTask subTask = new SubTask("SubTask", "Test");
        Task task = new Task("Task", "Test");

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);
        taskManager.createTask(task);

        Epic foundEpic = taskManager.getIdEpic(epic.getId());
        SubTask foundSubTask = taskManager.getIdSubtasks(subTask.getId());
        Task foundTask = taskManager.getIdTask(task.getId());

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
        Task task = new Task("Тестовая задача");

        Task taskCopy = new Task(task.getName());
        taskCopy.setId(task.getId());
        taskCopy.setStatus(task.getStatus());
        taskCopy.setDescription(task.getDescription());

        taskManager.createTask(task);

        assertEquals(taskCopy.getId(), task.getId());
        assertEquals(taskCopy.getName(), task.getName());
        assertEquals(taskCopy.getDescription(), task.getDescription());
        assertEquals(taskCopy.getStatus(), task.getStatus());
    }
    @Test
    @DisplayName("Проверка калькуляции статуса Эпика")
    void showCalculateEpicStatus() {
        Epic epic = new Epic("Test epic", "test");
        SubTask subTask1 = new SubTask("Test subTask1", "test_subTask1");
        SubTask subTask2 = new SubTask("Test subTask2", "test_subTask2");
        SubTask subTask3 = new SubTask("Test subTask3", "test_subTask3");

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        assertEquals(EnumStatus.NEW, epic.getStatus());

        subTask1.setStatus(EnumStatus.DONE);

        taskManager.calculateEpic(epic);

        assertEquals(EnumStatus.IN_PROGRESS, epic.getStatus());

        subTask1.setStatus(EnumStatus.DONE);
        subTask2.setStatus(EnumStatus.DONE);
        subTask3.setStatus(EnumStatus.DONE);

        taskManager.calculateEpic(epic);

        assertEquals(EnumStatus.DONE, epic.getStatus());
    }
    @Test
    @DisplayName("Проверка удаления Эпика и его Сабтасков")
    void showDeleteEpicAndItsSubTasks(){
        Epic epic1 = new Epic("Test epic1", "test1");
        SubTask subTask1 = new SubTask("Test subTask1", "test_subTask1");
        SubTask subTask2 = new SubTask("Test subTask2", "test_subTask2");

        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        Epic epic2 = new Epic("Test epic2", "test2");
        SubTask subTask3 = new SubTask("Test subTask3", "test_subTask3");
        SubTask subTask4 = new SubTask("Test subTask4", "test_subTask4");

        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask3);
        taskManager.createSubTask(subTask4);

        taskManager.deleteEpic(epic1.getId());

        int epicsSize = taskManager.getAllEpics().size();

        assertNotEquals(epic1.getSubTasks().size(),epic2.getSubTasks().size());
        assertEquals(1,epicsSize);
    }
    @Test
    @DisplayName("Проверка обновления эпиков и его сабтасков")
    void showUpdateEpicAndIstSubTasks(){
        Epic epic = new Epic("Test epic1", "test1");
        SubTask subTask1 = new SubTask("Test subTask1", "test_subTask1");
        SubTask subTask2 = new SubTask("Test subTask2", "test_subTask2");

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        String nameEpic = epic.getName();
        String descriptionEpic = epic.getDescription();
        epic.setName("new name");
        epic.setDescription("new description");

        taskManager.updateEpic(epic);

        assertNotEquals(nameEpic,epic.getName());
        assertNotEquals(descriptionEpic,epic.getDescription());

    }

}