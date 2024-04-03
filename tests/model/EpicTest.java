package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Эпик")
class EpicTest {
    TaskManager taskManager;
    @BeforeEach
    void init(){
        taskManager = Managers.getDefault();
    }
    @Test
    @DisplayName("Разные Эпики при создании одинкаовых экземпляров, но разных Id")
    void mustEpicBeEqualsEpicWithIdAndOtherParams(){
        Epic epic_1 = new Epic("Work in home", "All day");
        Epic epic_2 = new Epic("Work in home", "All day");
        taskManager.createEpic(epic_1);
        taskManager.createEpic(epic_2);

        assertNotEquals(epic_1,epic_2);
    }
    @Test
    @DisplayName("Разные Эпики при создании одинкаовых экземпляров и Id, но разных Сабтасках")
    void shouldBeDifferentInstancesOfEpicsIfSubTasksDoNotMatch(){
        Epic epic1 = new Epic("Test epic","test");
        SubTask subTask1 = new SubTask("Test subTask1","test_subTask1");
        SubTask subTask2 = new SubTask("Test subTask2","test_subTask2");

        Epic epic2 = new Epic("Test epic","test");
        SubTask subTask3 = new SubTask("Test subTask3","test_subTask3");
        SubTask subTask4 = new SubTask("Test subTask4","test_subTask4");

        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask3);
        taskManager.createSubTask(subTask4);
        epic2.setId(epic1.getId());
        //Устанавливаем одинаковое Id для Эпиков, что бы они были равны вне зависимости от генерации значения

        assertNotEquals(epic1,epic2);
    }

    @Test
    @DisplayName("Наличие обновления Сабтасков в Эпике через метод")
    void shouldAddSubTasksForEpicWithSetSubTaskVoid(){
        Epic epic = new Epic("Test epic","test");
        SubTask subTask1 = new SubTask("Test subTask1","test_subTask1");
        SubTask subTask2 = new SubTask("Test subTask2","test_subTask2");
        SubTask subTask3 = new SubTask("Test subTask3","test_subTask3");
        SubTask subTask4 = new SubTask("Test subTask4","test_subTask4");

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        taskManager.createSubTask(subTask4);

        List<SubTask> listForTest = new ArrayList<>();
        listForTest.add(subTask2);
        listForTest.add(subTask3);

        epic.setSubTasks(listForTest);

        int listSize = listForTest.size();
        int epicSubSize = epic.getSubTasks().size();

        assertEquals(epicSubSize,listSize);
    }

    @Test
    @DisplayName("Одинаковые Эпики при создании копии объекта")
    void shouldBeEqualsObjectsIfEpicMakeCopyEpic(){
        Epic epic = new Epic("test","epic test");
        SubTask subTask1 = new SubTask("Test subTask1","test_subTask1");
        SubTask subTask2 = new SubTask("Test subTask2","test_subTask2");
        Epic epicCopy = new Epic();

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createEpic(epicCopy);

        List<SubTask> listForTest = new ArrayList<>();
        listForTest.add(subTask1);
        listForTest.add(subTask2);

        epicCopy.setDescription(epic.getDescription());
        epicCopy.setName(epic.getName());
        epicCopy.setId(epic.getId());
        epicCopy.setSubTasks(listForTest);

        assertEquals(epic,epicCopy);

    }




    }