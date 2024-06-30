package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.ServerError;
import exceptions.TimePeriodsException;
import service.TaskManager;

import java.io.IOException;

public class ErrorHandler {

    private final TaskManager manager;
    public final Gson gson;

    public ErrorHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    public void handle(HttpExchange httpExchange, ServerError e) throws IOException {
        e.printStackTrace();
        BaseHttpHandler.sendServerError(httpExchange, gson.toJson(e.getMessage()));
    }

    public void handle(HttpExchange httpExchange, NotFoundException e) throws IOException {
        e.printStackTrace();
        BaseHttpHandler.sendNotFound(httpExchange, gson.toJson(e.getMessage()));
        //я пытался передавать объект класса new Error(e.getMessage()), но у меня постоянно возникала ошибка
        // com.google.gson.JsonIOException: Failed making field 'java.lang.Throwable#detailMessage' accessible;
        // either change its visibility or write a custom TypeAdapter for its declaring type
        //и как я только не пытался переписать адаптер и перевести в json detailMessage у меня так и не получилось....
    }

    public void handle(HttpExchange httpExchange, TimePeriodsException e) throws IOException {
        e.printStackTrace();
        BaseHttpHandler.sendHasInteractions(httpExchange, gson.toJson(e.getMessage()));
    }

    public void handle(HttpExchange httpExchange, Exception e) {
        //Я подумал, что лучше указать исключения явно. Фактически этот хэндл можно удалить и оставить только
        //перегруженные методы?
        System.out.println("Handling exception: " + e.getClass().getSimpleName());
        try {
            if (e instanceof ServerError) {
                handle(httpExchange, (ServerError) e);
                return;
            }
            if (e instanceof NotFoundException) {
                handle(httpExchange, (NotFoundException) e);
                return;
            }
            if (e instanceof TimePeriodsException) {
                handle(httpExchange, (TimePeriodsException) e);
                return;
            }
            e.printStackTrace();
            BaseHttpHandler.sendText(httpExchange, gson.toJson(new Error("Возникла ошибка в работе сервера")));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
}
