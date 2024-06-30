package server;

import com.google.gson.Gson;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import handlers.BaseHttpHandler;
import handlers.SubTasksHandler;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubTasksHandlersTests {
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    TaskManager manager = new InMemoryTaskManager(inMemoryHistoryManager);
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    Gson gson = BaseHttpHandler.getGson();
    public SubTasksHandlersTests() throws IOException {
    }

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
    public void SubTaskNotFoundExceptionWithoutEpic() throws IOException, InterruptedException {
            SubTask subTask = new SubTask("Test", "Testing", 12, 2021, 11, 12, 22, 11);
            subTask.setId(1);
            Integer id = subTask.getId();
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("http://localhost:8080/subtasks/" + id);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(404, response.statusCode());

    }
}
