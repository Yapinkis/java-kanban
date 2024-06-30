package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import handlers.BaseHttpHandler;
import model.Epic;
import model.SubTask;
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

public class HistoryHandlersTests {
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    TaskManager manager = new InMemoryTaskManager(inMemoryHistoryManager);
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    Gson gson = BaseHttpHandler.getGson();
    public HistoryHandlersTests() throws IOException {}

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
    public void HistoryHandlerGETRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing");
        SubTask subTask1 = new SubTask("Test1", "Testing", 12, 2024, 10, 8, 12, 10);
        SubTask subTask2 = new SubTask("Test2", "Testing", 12, 2024, 11, 12, 22, 11);

        manager.createEpic(epic);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Type taskListType = new TypeToken<ArrayList<SubTask>>(){}.getType();
        List<SubTask> tasksFromResponse = gson.fromJson(response.body(), taskListType);
        Assertions.assertEquals(2, tasksFromResponse.size());

    }

}
