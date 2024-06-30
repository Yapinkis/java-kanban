package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import exceptions.ServerError;
import exceptions.TimePeriodsException;
import model.EndpointMethod;
import model.Task;
import service.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class TasksHandler extends BaseHttpHandler {

     public TasksHandler(TaskManager manager) {
         super(manager);
     }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            try {
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");
                int pathLength = pathParts.length;
                Integer value = null;
                try {
                    value = toParse(pathParts);
                } catch (Exception e) {
                    System.out.println();
                }
                EndpointMethod endpointMethod = getEndpoint(exchange.getRequestMethod(), pathParts);
                switch (endpointMethod) {
                    case GET:
                        if (pathLength == 2) {
                            handleGetTasks(exchange);
                        } else {
                            handleGetTaskById(exchange, value);
                        }
                        break;
                    case POST:
                            try {
                                handleCreateTask(exchange);
                            } catch (Exception e) {
                                errorHandler.handle(exchange, new TimePeriodsException("Даты добавления  задач совпадают"));
                            }
                        break;
                    case DELETE:
                        deleteTask(exchange,value);
                        break;
                }
            } catch (Exception e) {
                errorHandler.handle(exchange, new ServerError("Server error"));
            }
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getAllTasks();
        String jsonResponse = gson.toJson(tasks);
        sendText(exchange, jsonResponse);
    }

    private void handleGetTaskById(HttpExchange exchange, Integer id) throws IOException {
            Task task = manager.getIdTask(id);
            if (task != null) {
                String taskToString = gson.toJson(task);
                sendText(exchange, taskToString);
            } else {
                errorHandler.handle(exchange, new NotFoundException("Id Задачи нот found"));
            }
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        String json = new BufferedReader(isr).lines().collect(Collectors.joining("\n"));
        Task newTask = gson.fromJson(json, Task.class);
        if (newTask.getId() == null) {
            manager.createTask(newTask);
            String taskToString = gson.toJson(newTask);
            sendCreate(exchange, taskToString);
        } else {
            manager.updateTask(newTask);
            String taskToString = gson.toJson(newTask);
            sendCreate(exchange,taskToString);
        }
    }

    private void deleteTask(HttpExchange exchange, Integer id) throws IOException {
        manager.deleteTask(id);
        String message = gson.toJson(new Error("Задача удалена"));
        sendText(exchange,message);
    }

}

