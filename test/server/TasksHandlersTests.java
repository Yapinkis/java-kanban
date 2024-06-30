package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import handlers.BaseHttpHandler;
import model.Task;
import org.junit.jupiter.api.*;
import service.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Server")
public class TasksHandlersTests {

    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    TaskManager manager = new InMemoryTaskManager(inMemoryHistoryManager);
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    Gson gson = BaseHttpHandler.getGson();
    public TasksHandlersTests() throws IOException {}

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
    public void TasksPOSTRequestCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",12,2021,11,12,22,11);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");

        Assertions.assertEquals(task.getName(),manager.getIdTask(1).getName());
        Assertions.assertEquals(task.getDescription(),manager.getIdTask(1).getDescription());
        Assertions.assertEquals(task.getStartTime(),manager.getIdTask(1).getStartTime());
        Assertions.assertEquals(task.getDuration(),manager.getIdTask(1).getDuration());
    }

    @Test
    public void TasksPOSTRequestUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",12,2021,11,12,22,11);
        manager.createTask(task);
        Task newTask = new Task(1,"NeName", "NewTest",12,2021,11,12,22,11);
        String taskJson = gson.toJson(newTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        Assertions.assertEquals(newTask.getName(),manager.getIdTask(1).getName());
        Assertions.assertEquals(newTask.getDescription(),manager.getIdTask(1).getDescription());
        Assertions.assertEquals(newTask.getStartTime(),manager.getIdTask(1).getStartTime());
        Assertions.assertEquals(newTask.getDuration(),manager.getIdTask(1).getDuration());
    }

    @Test
    public void TasksPOSTRequestAndCatchTimePeriodsException() throws IOException, InterruptedException {

        Task task1 = new Task("Test", "Testing task", 12, 2021, 11, 12, 22, 11);
        Task task2 = new Task("Test", "Testing task", 12, 2021, 11, 12, 22, 11);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        String jsonTask1 = gson.toJson(task1);
        String jsonTask2 = gson.toJson(task2);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .header("Content-Type", "application/json")
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask2))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());

    }
    @Test
    public void TasksGETRequest() throws IOException, InterruptedException {
        Task newTask1 = new Task("NeName1", "NewTest",12,2022,11,12,22,11);
        Task newTask2 = new Task("NeName2", "NewTest",12,2023,11,12,22,11);
        Task newTask3 = new Task("NeName3", "NewTest",12,2024,11,12,22,11);

        manager.createTask(newTask1);
        manager.createTask(newTask2);
        manager.createTask(newTask3);


        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Type taskListType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> tasksFromResponse =gson.fromJson(response.body(), taskListType);

        Assertions.assertEquals(3, tasksFromResponse.size());

    }

    @Test
    public void TasksGETRequestForTasksWithId() throws IOException, InterruptedException {
        Task newTask1 = new Task("NeName1", "NewTest",12,2022,11,12,22,11);

        manager.createTask(newTask1);

        Integer notNullId = newTask1.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + notNullId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Task notNullTask = gson.fromJson(response.body(), Task.class);
        Assertions.assertEquals(newTask1.getName(),notNullTask.getName());
        Assertions.assertEquals(newTask1.getDescription(),notNullTask.getDescription());
    }

    @Test
    public void TasksGETRequestForTasksWithoutId() throws IOException, InterruptedException {
        Task newTask1 = new Task("NeName1", "NewTest",12,2022,11,12,22,11);

        manager.createTask(newTask1);

        int NullId = 10;

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + NullId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
        //Здесь появится исключение, но так и должно быть по идее, тесты проходятся, а появление
        //exceptions.NotFoundException: Task Id not found. Или это хрень и у меня в консоль не должно ничего выводиться?

    }

}
