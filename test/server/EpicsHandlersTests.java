package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import handlers.BaseHttpHandler;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskServer;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class EpicsHandlersTests {
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    TaskManager manager = new InMemoryTaskManager(inMemoryHistoryManager);
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    Gson gson = BaseHttpHandler.getGson();
    public EpicsHandlersTests() throws IOException {}

    @BeforeEach
    public void setUp() {
        manager.clearAllTasks();
        manager.clearAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void EpicsReturnSubTasksListWithGETRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Testing", 12, 2023, 10, 8, 12, 10);
        Task task2 = new Task("Test1", "Testing", 12, 2022, 10, 8, 12, 10);
        Task task3 = new Task("Test1", "Testing", 12, 2024, 12, 8, 12, 10);
        Task task4 = new Task("Test1", "Testing", 12, 2021, 12, 8, 12, 10);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(task4);
        task3.setName("new");
        manager.updateTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Type taskListType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> tasksFromResponse = gson.fromJson(response.body(), taskListType);
        Assertions.assertEquals(4, tasksFromResponse.size());
        List<Task> taskFromManager = manager.getAllTasks();

        for (int i = 0; i < tasksFromResponse.size();i++) {
            Assertions.assertEquals(tasksFromResponse.get(i).getName(),taskFromManager.get(i).getName());
            Assertions.assertEquals(tasksFromResponse.get(i).getDescription(),taskFromManager.get(i).getDescription());
            Assertions.assertEquals(tasksFromResponse.get(i).getId(),taskFromManager.get(i).getId());
            Assertions.assertEquals(tasksFromResponse.get(i).getDuration(),taskFromManager.get(i).getDuration());
            Assertions.assertEquals(tasksFromResponse.get(i).getStartTime(),taskFromManager.get(i).getStartTime());
        }

    }
}
