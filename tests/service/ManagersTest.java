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
    InMemoryHistoryManager historyManager;
    InMemoryTaskManager taskManager;
    @BeforeEach
    void init(){
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        historyManager = new InMemoryHistoryManager();
        //В перспективе будущих тестов, наверное лучше оставить так...
    }
    @DisplayName("Возвращает рабочие экзмепляры")
    @Test
    void managerReturnWorkingInstances(){

        taskManager.epics.put(0,new Epic());
        taskManager.subTasks.put(0,new SubTask());
        taskManager.tasks.put(0,new Task());

        historyManager.history.add(new Task());

        assertNotNull(taskManager.epics);
        assertNotNull(taskManager.subTasks);
        assertNotNull(taskManager.tasks);

        assertNotNull(historyManager.history);

    }
}