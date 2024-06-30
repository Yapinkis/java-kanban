package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ServerError;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String request = exchange.getRequestMethod();
            if (request.equals("GET")) {
                handleGetPrioritizedTasks(exchange);
            }
        } catch (Exception e) {
            errorHandler.handle(exchange, new ServerError("Server error"));
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        List<Task> history = new ArrayList<>(manager.getPrioritizedTasks());
        String jsonResponse = gson.toJson(history);
        sendText(exchange, jsonResponse);
    }
}
