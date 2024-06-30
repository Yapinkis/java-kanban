package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ServerError;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String request = exchange.getRequestMethod();
            if (request.equals("GET")) {
                handleGetHistory(exchange);
            }
        } catch (Exception e) {
            errorHandler.handle(exchange, new ServerError("Server error"));
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> history = manager.getHistoryHManager();
        String jsonResponse = gson.toJson(history);
        sendText(exchange, jsonResponse);
    }
}
