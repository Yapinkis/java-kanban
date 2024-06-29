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
class ManagersTest extends TaskManagerTest <InMemoryTaskManager>{
    InMemoryHistoryManager inMemoryHistoryManager;

    @Override
    protected InMemoryTaskManager createTaskManager() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
        return new InMemoryTaskManager(inMemoryHistoryManager);
    }
    @BeforeEach
    void init() {
        super.setUp();
    }

    @DisplayName("Возвращает рабочие экзмепляры")
    @Test
    void managerReturnWorkingInstances() {

        TaskManager.createEpic(new Epic("Test_Epic","test"));
        TaskManager.createSubTask(new SubTask("Test_SubTask","test"));
        TaskManager.createTask(new Task("Test_Task","test"));

        assertNotNull(TaskManager.getAllEpics());
        assertNotNull(TaskManager.getAllSubTasks());
        assertNotNull(TaskManager.getAllTasks());
    }
}