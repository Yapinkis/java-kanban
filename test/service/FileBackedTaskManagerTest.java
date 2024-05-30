package service;

import exceptions.ManagerSaveException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Работа: File Backed Task Manager")
public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    InMemoryHistoryManager inMemoryHistoryManager;
    private Path path;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        this.inMemoryHistoryManager = new InMemoryHistoryManager();
        return new FileBackedTaskManager(this.inMemoryHistoryManager);
    }

    @BeforeEach
    void init() throws IOException {
        super.setUp();
        path = Paths.get("resources/TasksHistory.csv");
        Path backupPath = Paths.get("resources/BrowsingHistory.csv");
        Files.write(path, Collections.emptyList());
        Files.write(backupPath, Collections.emptyList());
    }

    @DisplayName("Проверка сохранения задач в файл")
    @Test
    void checkingHistoryIsSavedToFile() {

        Task task1 = new Task("otherTask_1", "otherTest1");
        Task task2 = new Task("otherTask_2", "otherTest2");
        Task task3 = new Task("otherTask_3", "otherTest3");


        TaskManager.createTask(task1);
        TaskManager.createTask(task2);
        TaskManager.createTask(task3);

        assertNotNull(path);
    }

    @DisplayName("Проверка сохранения разных типов задач в файл")
    @Test
    void checkingHistoryIsSavingOfDifferentTypesOfTasks() throws IOException {

        Task task1 = new Task("Task_1", "Test1",10,FixingTimeTest.startTime);
        Epic epic1 = new Epic("Epic_1", "Test2");
        SubTask subTask1 = new SubTask("SubTask_1", "Test3");
        Epic epic2 = new Epic("Epic_2", "Test4");
        SubTask subTask2 = new SubTask("SubTask_2", "Test5",12,FixingTimeTest.startTime);

        TaskManager.createTask(task1);
        TaskManager.createEpic(epic1);
        TaskManager.createSubTask(subTask1);
        TaskManager.createEpic(epic2);
        TaskManager.createSubTask(subTask2);

        List<String> strings = Files.readAllLines(path);
        String[] words = new String[strings.size()];
        int temp = 0;
        for (int i = 2; i < strings.size(); i += 4) {
            String[] variable = strings.get(i).split(",\\s*");
            words[temp] = variable[1];
            temp++;
        }
        assertEquals(TasksType.valueOf(words[0]), TasksType.TASK);
        assertEquals(TasksType.valueOf(words[1]), TasksType.EPIC);
        assertEquals(TasksType.valueOf(words[2]), TasksType.SUBTASK);
        assertEquals(TasksType.valueOf(words[3]), TasksType.EPIC);
        assertEquals(TasksType.valueOf(words[4]), TasksType.SUBTASK);

    }

    @DisplayName("Проверка создания задач из сохранённого файла истории")
    @Test
    void checkingTheCreationTasksFromASavedHistory() {

        Task task1 = new Task("someTask_1", "someTest1");
        Epic epic1 = new Epic("someEpic_1", "someTest2");
        SubTask subTask1 = new SubTask("someSubTask_1", "someTest3");
        Epic epic2 = new Epic("someEpic_2", "someTest4");
        SubTask subTask2 = new SubTask("someSubTask_2", "someTest5");

        TaskManager.createTask(task1);
        TaskManager.createEpic(epic1);
        TaskManager.createSubTask(subTask1);
        TaskManager.createEpic(epic2);
        TaskManager.createSubTask(subTask2);

        TaskManager.clearAllTasks();
        TaskManager.clearAllEpics();

        TaskManager = FileBackedTaskManager.loadFromFile(path.toFile());

        String taskName = TaskManager.getIdTask(1).getName();
        String epicDescription = TaskManager.getIdEpic(2).getDescription();
        TasksType subTaskTStatus = TaskManager.getSubTasks(epic2).get(0).getTasksType();
        int allEpics = TaskManager.getAllEpics().size();

        assertEquals(task1.getName(), taskName);
        assertEquals(epic1.getDescription(), epicDescription);
        assertEquals(subTask1.getTasksType(), subTaskTStatus);
        assertNotEquals(0, allEpics);
    }

    @DisplayName("Проверка исключений при передаче ссылки на файл который отсутствует")
    @Test
    void errorReadingTheFile_ManagerSaveException() {
        Path file = Paths.get("resources/someFile404.csv");

        assertThrows(ManagerSaveException.class,() -> {TaskManager =
                FileBackedTaskManager.loadFromFile(file.toFile());});
    }
}

