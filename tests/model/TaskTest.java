package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Таск")
class TaskTest {
    TaskManager taskManager;
    @BeforeEach
    void init(){
        taskManager = Managers.getDefault();
    }

    @Test
    @DisplayName("Две одинаковые задачи совпдают, если у них одинаковый Id")
    void mustTaskBeEqualsTaskWithIdAndOtherParams(){
        Task task1 = new Task("Work in home", "All day");
        Task task2 = new Task("Work in home", "All day");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        task2.setId(task1.getId());

        assertEquals(task1,task2);
    }


}