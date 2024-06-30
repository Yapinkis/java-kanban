package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import exceptions.ServerError;
import exceptions.TimePeriodsException;
import model.EndpointMethod;
import model.SubTask;
import service.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class SubTasksHandler extends BaseHttpHandler {

    public SubTasksHandler(TaskManager manager) {
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
                    case GET: {
                        if (pathLength == 2) {
                            handleGetSubTasks(exchange);
                        } else {
                            handleGetSubTasksById(exchange, value);
                        }
                    }
                    break;
                    case POST:
                        try {
                            handleCreateSubTasks(exchange);
                        } catch (Exception e) {
                            errorHandler.handle(exchange, new TimePeriodsException("Даты добавления  задач совпадают"));
                        }
                        break;
                    case DELETE:
                        deleteSubTask(exchange,value);
                        break;
                }
            } catch (Exception e) {
                errorHandler.handle(exchange, new ServerError("Server error"));
            }

        }
    }

    private void handleGetSubTasks(HttpExchange exchange) throws IOException {
        List<SubTask> subTasks = manager.getAllSubTasks();
        String jsonResponse = gson.toJson(subTasks);
        sendText(exchange, jsonResponse);
    }
    //Код частично дублируется, нужно ли создавать какой-то абстрактный класс для таких случаев или интерфейс?
    //Или всё это можно было определить в BaseHttpHandler а здесь просто сделать @Override

    private void handleGetSubTasksById(HttpExchange exchange, Integer id) throws IOException {
        SubTask subTask = manager.getIdSubtasks(id);
        if (subTask != null) {
            String taskToString = gson.toJson(subTask);
            sendText(exchange, taskToString);
        } else {
            errorHandler.handle(exchange, new NotFoundException("Сабтаск Id нот found"));
        }
    }

    private void handleCreateSubTasks(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        String json = new BufferedReader(isr).lines().collect(Collectors.joining("\n"));
        SubTask newSubTasks = gson.fromJson(json, SubTask.class);
        if (newSubTasks.getId() == null) {
            try {
                manager.createSubTask(newSubTasks);
                String subToString = gson.toJson(newSubTasks);
                sendCreate(exchange, subToString);
            } catch (Exception e) {
                errorHandler.handle(exchange, new NotFoundException("Эпик для подзадач не обнаружен"));
            }
        } else {
            manager.updateSubTask(newSubTasks);
            String subToString = gson.toJson(newSubTasks);
            sendCreate(exchange,subToString);
        }
    }

    private void deleteSubTask(HttpExchange exchange, Integer id) throws IOException {
        manager.deleteSubTask(id);
        String message = gson.toJson(new Error("Подзадача удалена"));
        sendText(exchange,message);
    }

}
