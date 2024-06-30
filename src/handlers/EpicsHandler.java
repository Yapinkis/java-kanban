package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.ServerError;
import model.EndpointMethod;
import model.Epic;
import model.SubTask;
import service.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager manager) {
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
                    case GET :
                        if (pathLength == 2) {
                            handleGetEpics(exchange);
                        } else if (pathParts[pathLength - 1].equals("subtasks")) {
                            handleGetSubtasksByEpc(exchange,value);
                        } else {
                            handleGetEpicsById(exchange,value);
                        }
                        break;
                    case POST:
                        handleCreateEpic(exchange);
                        break;
                    case DELETE:
                        deleteEpic(exchange,value);
                        break;
                }
            } catch (Exception e) {
                errorHandler.handle(exchange, new ServerError("Server error"));
            }
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getAllEpics();
        String jsonResponse = gson.toJson(epics);
        sendText(exchange, jsonResponse);
    }

    private void handleGetEpicsById(HttpExchange exchange, Integer id) throws IOException {
        Epic epic = manager.getIdEpic(id);
        if (epic != null) {
            String taskToString = gson.toJson(epic);
            sendText(exchange, taskToString);
        } else {
            errorHandler.handle(exchange, new NotFoundException("Epic Id not found"));
        }
    }

    private void handleGetSubtasksByEpc(HttpExchange exchange, Integer id) throws IOException {
        List<Integer> subTasksId = manager.getIdEpic(id).getSubTasks();
        if (subTasksId != null) {
            List<SubTask> subTasks = new ArrayList<>();
            for (Integer subTaskId : subTasksId) {
                SubTask subTask = manager.getIdSubtasks(subTaskId); // Предполагается, что есть метод для получения SubTask по ID
                if (subTask != null) {
                    subTasks.add(subTask);
                }
            }
            String taskToString = gson.toJson(subTasks);
            sendText(exchange, taskToString);
        } else {
            errorHandler.handle(exchange, new NotFoundException("Сабтаски отсутствуют"));
        }
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        String json = new BufferedReader(isr).lines().collect(Collectors.joining("\n"));
        Epic newEpic = gson.fromJson(json, Epic.class);
        manager.createEpic(newEpic);
        String subToString = gson.toJson(newEpic);
        sendCreate(exchange, subToString);
    }

    private void deleteEpic(HttpExchange exchange, Integer id) throws IOException {
        manager.deleteEpic(id);
        String message = gson.toJson(new Error("Эпик удалён"));
        sendText(exchange,message);
    }

}

