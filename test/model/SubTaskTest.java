package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("СабТаск")
class SubTaskTest {
    TaskManager taskManager;
    @BeforeEach
    void init(){
        taskManager = Managers.getDefault();
    }

    @Test
    @DisplayName("Сабтаски не будут совпадать, если у них одинаковые поля, но разыне Эпики")
    void mustSubTaskBeNotEqualsSubTaskWithIdAndOtherParams(){
        Epic epic1 = new Epic("Worker","forTest_1");
        SubTask subTask1 = new SubTask("Work in home", "All day");
        SubTask subTask2 = new SubTask("Work in home", "All day");

        Epic epic2 = new Epic("Worker","forTest_2");
        SubTask subTask3 = new SubTask("Work in home", "All day");
        SubTask subTask4 = new SubTask("Work in home", "All day");

        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask3);
        taskManager.createSubTask(subTask4);

        subTask3.setId(subTask1.getId());
        subTask4.setId(subTask2.getId());

        assertNotEquals(subTask1,subTask3);
        assertNotEquals(subTask2,subTask4);
    }
    @Test
    @DisplayName("Сабтаски будут сопадать, если у них одинаковые поля и одинаковые экземпляры Эпика")
    void mustSubTaskBeEqualsSubTaskWithOneEpicObjectCopy(){
        Epic epic1 = new Epic("Worker","forTest_1");
        SubTask subTask1 = new SubTask("Work in home", "All day");

        Epic epic2 = new Epic("Worker","forTest_1");
        SubTask subTask2 = new SubTask("Work in home", "All day");

        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask2);

        epic2.setId(epic1.getId());
        epic2.setName(epic1.getName());
        epic2.setDescription(epic1.getDescription());
        subTask2.setId(subTask1.getId());

        assertEquals(subTask1.getName(), subTask2.getName(),
                String.format("Имя эпиков не совпадает, ошибка в параметре Name: %s. Фактическое значение: %s",
                        subTask1.getName(), subTask2.getName()));
        assertEquals(subTask1.getDescription(), subTask2.getDescription(),
                String.format("Задачи эпиков не совпадают, ошибка в параметре Description: %s. " +
                        "Фактическое значение: %s", subTask1.getDescription(), subTask2.getDescription()));
        assertEquals(subTask1.getId(), subTask2.getId(),
                String.format("Идентификатор эпиков не совпадает, ошибка в параметре Id: %s. " +
                        "Фактическое значение: %s", subTask1.getId(), subTask2.getId()));
    }

    @DisplayName("Проверка исключений при попытке создать сабтаск без Эпика")
    @Test
    void errorTryCreateSubTaskWithoutEpic_NullPointerException() {
        SubTask subTask1 = new SubTask("Test1","forTest");
        SubTask subTask2 = new SubTask("Test2","forTest");

        assertThrows(NullPointerException.class,() -> {
            taskManager.createSubTask(subTask1);
            taskManager.createSubTask(subTask2);});
    }


}
