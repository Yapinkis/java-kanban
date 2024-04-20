package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Менеджер")
class ManagersTest {
    TaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
    }

    @DisplayName("Возвращает рабочие экзмепляры")
    @Test
    void managerReturnWorkingInstances() {

        taskManager.createEpic(new Epic("Test_Epic"));
        taskManager.createSubTask(new SubTask("Test_SubTask"));
        taskManager.createTask(new Task("Test_Task"));

        assertNotNull(taskManager.getAllEpics());
        assertNotNull(taskManager.getAllSubTasks());
        assertNotNull(taskManager.getAllTasks());
    }
}