package service;

import com.sun.net.httpserver.HttpServer;
import handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.taskManager = manager;
        initializeServer();
    }

        private void initializeServer() throws IOException {
            httpServer = HttpServer.create(new InetSocketAddress(PORT),0);
            httpServer.createContext("/tasks", new TasksHandler(taskManager));
            httpServer.createContext("/subtasks", new SubTasksHandler(taskManager));
            httpServer.createContext("/epics",new EpicsHandler(taskManager));
            httpServer.createContext("/history", new HistoryHandler(taskManager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT + "!");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }

}
