package handlers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import httpresources.Adapters;
import model.EndpointMethod;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler implements HttpHandler {
    Gson gson;
    TaskManager manager;
    ErrorHandler errorHandler;

    public BaseHttpHandler(TaskManager taskManager) {
        this.manager = taskManager;
        this.gson = getGson();
        this.errorHandler = new ErrorHandler(taskManager,this.gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }

    public static void sendText(HttpExchange exchange, String message) throws IOException {
        byte [] byteArr = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200,byteArr.length);
        exchange.getResponseBody().write(byteArr);
        exchange.close();
    }

    public static void sendCreate(HttpExchange exchange, String message) throws IOException {
        byte [] byteArr = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(201,byteArr.length);
        exchange.getResponseBody().write(byteArr);
        exchange.close();
    }

    public static void sendNotFound(HttpExchange exchange, String message) throws IOException {
        String text = "Задача не была обнаружена " + message;
        byte [] byteArr = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404,byteArr.length);
        exchange.getResponseBody().write(byteArr);
        exchange.close();
    }

    public static void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        String text = "Временные интервалы задач пересекаются " + message;
        byte [] byteArr = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406,byteArr.length);
        exchange.getResponseBody().write(byteArr);
        exchange.close();
    }

    public static void sendServerError(HttpExchange exchange, String message) throws IOException {
        String text = "Временные интервалы задач пересекаются " + message;
        byte [] byteArr = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(500,byteArr.length);
        exchange.getResponseBody().write(byteArr);
        exchange.close();
    }

    public static EndpointMethod getEndpoint(String requestMethod, String[] path) {
        if (path.length == 2) {
            if (requestMethod.equals("GET")) {
                return EndpointMethod.GET;
            } else {
                return EndpointMethod.POST;
            }
        }
        if (path.length > 2) {
            if (requestMethod.equals("GET")) {
                return EndpointMethod.GET;
            } else {
                return EndpointMethod.DELETE;
            }
        }
        return EndpointMethod.UNKNOWN;
    }

    public static int toParse(String[] path) {
        for (String segment : path) {
            String parseNumber = segment.replaceAll("[^0-9]", "");
            if (!parseNumber.isEmpty()) {
                return Integer.parseInt(parseNumber);
            }
        }
        return 0;
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new Adapters.LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new Adapters.DurationAdapter());
        gsonBuilder.registerTypeAdapter(Throwable.class, new Adapters.ThrowableTypeAdapter());
        return gsonBuilder.create();
    }

}
