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
    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    void init() {
        inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @DisplayName("Возвращает рабочие экзмепляры")
    @Test
    void managerReturnWorkingInstances() {

        inMemoryTaskManager.epics.put(0, new Epic());
        inMemoryTaskManager.subTasks.put(0, new SubTask());
        inMemoryTaskManager.tasks.put(0, new Task());

        assertNotNull(inMemoryTaskManager.epics.put(0, new Epic()));
        assertNotNull(inMemoryTaskManager.subTasks.put(0, new SubTask()));
        assertNotNull(inMemoryTaskManager.tasks.put(0, new Task()));
    }
}