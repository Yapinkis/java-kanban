package service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Работа: File Backed Task Manager")
public class FileBackedTaskManagerTest {
    TaskManager taskManager;
    InMemoryHistoryManager inMemoryHistoryManager;
    FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void init(){
        taskManager = Managers.getDefault();
        inMemoryHistoryManager = new InMemoryHistoryManager();
        fileBackedTaskManager = new FileBackedTaskManager(inMemoryHistoryManager);
    }

    @DisplayName("Проверка сохранения задач в файл")
    @Test
    void checkingHistoryIsSavedToFile() throws IOException {
        Path path = Paths.get("resources/TasksHistory.csv");
        File file = path.toFile();
        Files.write(path, Collections.emptyList());
        //очищаем содержимое файла, для тестирования каждого метода по-отдельности

        Task task1 = new Task("Task_1","Test1");
        Task task2 = new Task("Task_2","Test2");
        Task task3 = new Task("Task_3","Test3");

        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createTask(task2);
        fileBackedTaskManager.createTask(task3);

        assertNotNull(file);
    }

    @DisplayName("Проверка сохранения разных типов задач в файл")
    @Test
    void checkingHistoryIsSavingOfDifferentTypesOfTasks() throws IOException {
        Path path = Paths.get("resources/TasksHistory.csv");
        Files.write(path, Collections.emptyList());

        Task task1 = new Task("Task_1","Test1");
        Epic epic1 = new Epic("Epic_1","Test2");
        SubTask subTask1 = new SubTask("SubTask_1", "Test3");
        Epic epic2 = new Epic("Epic_2","Test4");
        SubTask subTask2 = new SubTask("SubTask_2", "Test5");

        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createSubTask(subTask1);
        fileBackedTaskManager.createEpic(epic2);
        fileBackedTaskManager.createSubTask(subTask2);

        List<String> strings = Files.readAllLines(path);
        String[] words = new String[strings.size()-1];
        for (int i = 1; i < strings.size();i++){
            String[] variable = strings.get(i).split(",\\s*");
            words[i-1] = variable[1];
        }
        assertEquals(TasksStatus.valueOf(words[0]),TasksStatus.TASK);
        assertEquals(TasksStatus.valueOf(words[1]),TasksStatus.EPIC);
        assertEquals(TasksStatus.valueOf(words[2]),TasksStatus.SUBTASK);
        assertEquals(TasksStatus.valueOf(words[3]),TasksStatus.EPIC);
        assertEquals(TasksStatus.valueOf(words[4]),TasksStatus.SUBTASK);

    }

    @DisplayName("Проверка создания задач из сохранённого файла истории")
    @Test
    void checkingTheCreationTasksFromASavedHistory() throws IOException{
        Path path = Paths.get("resources/TasksHistory.csv");
        Files.write(path, Collections.emptyList());

        Task task1 = new Task("Task_1","Test1");
        Epic epic1 = new Epic("Epic_1","Test2");
        SubTask subTask1 = new SubTask("SubTask_1", "Test3");
        Epic epic2 = new Epic("Epic_2","Test4");
        SubTask subTask2 = new SubTask("SubTask_2", "Test5");

        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createSubTask(subTask1);
        fileBackedTaskManager.createEpic(epic2);
        fileBackedTaskManager.createSubTask(subTask2);

        fileBackedTaskManager.clearAllTasks();
        fileBackedTaskManager.clearAllEpics();
        fileBackedTaskManager.clearAllEpics();

        fileBackedTaskManager =  FileBackedTaskManager.loadFromFile(path.toFile());

        String taskName = fileBackedTaskManager.getIdTask(1).getName();
        String epicDescription = fileBackedTaskManager.getIdEpic(2).getDescription();
        TasksStatus subTaskTStatus = fileBackedTaskManager.getIdEpic(2).getSubTasks().get(0).getTaskStatus();
        int allEpics = fileBackedTaskManager.getAllEpics().size();

        assertEquals(task1.getName(),taskName);
        assertEquals(epic1.getDescription(),epicDescription);
        assertEquals(subTask1.getTaskStatus(),subTaskTStatus);
        assertNotEquals(0,allEpics);
    }

}
