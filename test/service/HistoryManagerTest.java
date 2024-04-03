package service;

import model.Epic;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Работа: History Manager Test")
class HistoryManagerTest {
    InMemoryHistoryManager historyManager;
    @BeforeEach
    void init(){
        historyManager = new InMemoryHistoryManager();
    }

    @DisplayName("Проверка сохранения предыдущих версий задач")
    @Test
    void catchPreviousHistoryElement(){

        Task task1 = new Task("Task1", "test_1");
        Task task2 = new Task("Task2", "test_2");
        Task task3 = new Task("Task3", "test_3");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        Task catchTask = historyManager.getHistory().get(historyManager.getHistory().size()-2);
        assertEquals(task2,catchTask);
    }

    @DisplayName("Проверка работы HistoryManager при наличии >10 элементов")
    @Test
    void showHistoryWith10orMoreElements(){
        Task task1 = new Task("Task1", "test_1");
        Task task2 = new Task("Task2", "test_2");
        Task task3 = new Task("Task3", "test_3");
        Task task4 = new Task("Task4", "test_4");
        Task task5 = new Task("Task5", "test_5");
        Task task6 = new Task("Task6", "test_6");
        Epic epic1 = new Epic("Epic1", "test_1");
        Epic epic2 = new Epic("Epic2", "test_2");
        Epic epic3 = new Epic("Epic3", "test_3");
        Epic epic4 = new Epic("Epic4", "test_4");
        Epic epic5 = new Epic("Epic5", "test_5");
        Epic epic6 = new Epic("Epic6", "test_6");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(epic3);
        historyManager.add(epic4);
        historyManager.add(epic5);
        historyManager.add(epic6);

        int sizeHistory = historyManager.getHistory().size();

        assertEquals(10,sizeHistory);
        assertEquals(epic6,historyManager.history.get(9));
    }

}