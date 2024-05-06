package service;

import model.Epic;
import model.SubTask;
import model.Task;
import service.InMemoryHistoryManager.Node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Работа: In Memory History Manager")
class InMemoryHistoryManagerTest {
    TaskManager taskManager;
    InMemoryHistoryManager inMemoryHistoryManager;
    @BeforeEach
    void init(){
        taskManager = Managers.getDefault();
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }

    @DisplayName("Проверка обновления head и tail")
    @Test
    void checkingForHeadAndTailUpdates(){
        Task task1 = new Task("Test1","test");
        Task task2 = new Task("Test2","test");
        Task task3 = new Task("Test3","test");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        inMemoryHistoryManager.linkLast(task1);
        inMemoryHistoryManager.linkLast(task2);

        InMemoryHistoryManager.Node head = inMemoryHistoryManager.getMap().get(task1.getId());
        InMemoryHistoryManager.Node tailBeforeAdd = inMemoryHistoryManager.getMap().get(task2.getId());

        inMemoryHistoryManager.linkLast(task3);

        InMemoryHistoryManager.Node tailAfterAdd = inMemoryHistoryManager.getMap().get(task3.getId());

        assertEquals(head, inMemoryHistoryManager.getHead());
        assertNotEquals(tailBeforeAdd,tailAfterAdd);
    }
    @DisplayName("Проверка заполнения узлов списка")
    @Test
    void checkingForNodeUpdates(){

        Task task1 = new Task("Test1","test");
        Task task2 = new Task("Test2","test");
        Task task3 = new Task("Test3","test");
        Task task4 = new Task("Test4","test");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);

        inMemoryHistoryManager.linkLast(task3);
        inMemoryHistoryManager.linkLast(task2);
        inMemoryHistoryManager.linkLast(task4);
        inMemoryHistoryManager.linkLast(task1);

        Node preNode = inMemoryHistoryManager.getMap().get(task3.getId());
        Node Node = inMemoryHistoryManager.getMap().get(task2.getId());
        Node lastNode = inMemoryHistoryManager.getMap().get(task4.getId());

        assertNotNull(preNode);
        assertNotNull(Node);
        assertNotNull(lastNode);
    }
    @DisplayName("Проверка наличия задачи в менеджере и Id")
    @Test
    void canAddTasksAndHeirsAndCanFindThemById(){

        Epic epic = new Epic("Epic","Test");
        SubTask subTask = new SubTask("SubTask","Test");
        Task task = new Task("Task","Test");

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);
        taskManager.createTask(task);

        assertNotNull(taskManager.getIdEpic(epic.getId()));
        assertNotNull(taskManager.getIdSubtasks(subTask.getId()));
        assertNotNull(taskManager.getIdTask(task.getId()));
    }
    @DisplayName("Проверка соответствия по Id")
    @Test
    void canFindById(){

        Epic epic = new Epic("Epic","Test");
        SubTask subTask = new SubTask("SubTask","Test");
        Task task = new Task("Task","Test");

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);
        taskManager.createTask(task);

        Epic foundEpic = taskManager.getIdEpic(epic.getId());
        SubTask foundSubTask = taskManager.getIdSubtasks(subTask.getId());
        Task foundTask = taskManager.getIdTask(task.getId());

        assertEquals(epic, foundEpic);
        assertEquals(subTask, foundSubTask);
        assertEquals(task, foundTask);
    }
    @DisplayName("Проверка удаления задач методом remove")
    @Test
    void checkingTheRemovalOfTasksUsingTheRemoveMethod(){
        Epic epic_1 = new Epic("Epic_1","Test");
        SubTask subTask_1_1 = new SubTask("SubTask_1_1","Test");
        SubTask subTask_1_2 = new SubTask("SubTask_1_2","Test");
        Task task_1_1 = new Task("Task_1_1","Test");
        Task task_1_2 = new Task("Task_1_2","Test");

        inMemoryHistoryManager.add(epic_1);
        inMemoryHistoryManager.add(subTask_1_1);
        inMemoryHistoryManager.add(subTask_1_2);
        inMemoryHistoryManager.add(task_1_1);
        inMemoryHistoryManager.add(task_1_2);

        int beforeDel = inMemoryHistoryManager.getHistory().size();
        Map<Integer, Node> map = inMemoryHistoryManager.getMap();
        inMemoryHistoryManager.remove(map.get(epic_1.getId()));
        int afterDel = inMemoryHistoryManager.getHistory().size();
        assertNotEquals(beforeDel, afterDel);
    }
    @DisplayName("Проверка отсутствия дублирования задач и их количества")
    @Test
    void checkingThatThereIsNoDuplicationOfTasks(){
        Epic epic_1 = new Epic("Epic_1","Test");
        SubTask subTask_1_1 = new SubTask("SubTask_1_1","Test");
        SubTask subTask_1_2 = new SubTask("SubTask_1_2","Test");
        Task task_1_1 = new Task("Task_1_1","Test");
        Task task_1_2 = new Task("Task_1_2","Test");

        taskManager.createEpic(epic_1);
        taskManager.createSubTask(subTask_1_1);
        taskManager.createSubTask(subTask_1_2);
        taskManager.createTask(task_1_1);
        taskManager.createTask(task_1_2);

        int tasksCount = 0;

        inMemoryHistoryManager.add(epic_1);
        ++tasksCount;
        inMemoryHistoryManager.add(subTask_1_1);
        ++tasksCount;
        inMemoryHistoryManager.add(subTask_1_2);
        ++tasksCount;
        inMemoryHistoryManager.add(task_1_1);
        ++tasksCount;
        inMemoryHistoryManager.add(task_1_2);
        ++tasksCount;
        inMemoryHistoryManager.add(task_1_1);
        ++tasksCount;
        inMemoryHistoryManager.add(task_1_2);
        ++tasksCount;
        int historySize = inMemoryHistoryManager.getHistory().size();
        List<Task> history = inMemoryHistoryManager.getHistory();

        assertNotEquals(historySize,tasksCount);
        for(int i = 0;i < history.size();i++){
            Task current = history.get(i);
            for(int j = i + 1;j < history.size();j++){
                Task otherTask = history.get(j);
                assertNotEquals(current,otherTask);
            }
        }
    }
    @DisplayName("Проверка наличия добавленных задач в список")
    @Test
    void сheckingForAddedTasksToTheList(){
        Epic epic_1 = new Epic("Epic_1","Test");
        SubTask subTask_1_1 = new SubTask("SubTask_1_1","Test");
        SubTask subTask_1_2 = new SubTask("SubTask_1_2","Test");
        Task task_1_1 = new Task("Task_1_1","Test");
        Task task_1_2 = new Task("Task_1_2","Test");

        taskManager.createEpic(epic_1);
        taskManager.createSubTask(subTask_1_1);
        taskManager.createSubTask(subTask_1_2);
        taskManager.createTask(task_1_1);
        taskManager.createTask(task_1_2);


        inMemoryHistoryManager.add(epic_1);
        inMemoryHistoryManager.add(subTask_1_1);
        inMemoryHistoryManager.add(subTask_1_2);
        inMemoryHistoryManager.add(task_1_1);
        inMemoryHistoryManager.add(task_1_2);

        List<Task> tasks = inMemoryHistoryManager.getHistory();

        assertEquals(tasks.get(0),epic_1);
        assertEquals(tasks.get(1),subTask_1_1);
        assertEquals(tasks.get(2),subTask_1_2);
        assertEquals(tasks.get(3),task_1_1);
        assertEquals(tasks.get(4),task_1_2);
    }
    @DisplayName("Проверка изменения истории задач, в зависимости от рандомизации порядка добавления")
    @Test
    void checkingForChangesInTheTaskHistoryDependingnRandomization(){
        Epic epic_1 = new Epic("Epic_1","Test");
        SubTask subTask_1_1 = new SubTask("SubTask_1_1","Test");
        SubTask subTask_1_2 = new SubTask("SubTask_1_2","Test");
        Task task_1_1 = new Task("Task_1_1","Test");
        Task task_1_2 = new Task("Task_1_2","Test");

        taskManager.createEpic(epic_1);
        taskManager.createSubTask(subTask_1_1);
        taskManager.createSubTask(subTask_1_2);
        taskManager.createTask(task_1_2);
        taskManager.createTask(task_1_1);

        inMemoryHistoryManager.add(epic_1);
        inMemoryHistoryManager.add(subTask_1_2);
        inMemoryHistoryManager.add(subTask_1_1);
        inMemoryHistoryManager.add(task_1_1);
        inMemoryHistoryManager.add(task_1_2);

        List<Task> oldHistory = inMemoryHistoryManager.getHistory();

        inMemoryHistoryManager.add(task_1_2);
        inMemoryHistoryManager.add(task_1_1);
        inMemoryHistoryManager.add(subTask_1_2);
        inMemoryHistoryManager.add(subTask_1_1);
        inMemoryHistoryManager.add(epic_1);

        List<Task> newHistory = inMemoryHistoryManager.getHistory();
        int historySize = inMemoryHistoryManager.getHistory().size();

        for(int i = 0;i < historySize;i++){
            assertNotEquals(oldHistory.get(i),newHistory.get(i));
        }
    }
    @DisplayName("После удаления задачи, она остаётся в истории")
    @Test
    void afterDeletingTaskInformationAboutItRemainsInHistory(){
        Epic epic_1 = new Epic("Epic_1","Test");
        SubTask subTask_1_1 = new SubTask("SubTask_1_1","Test");
        SubTask subTask_1_2 = new SubTask("SubTask_1_2","Test");
        Task task_1_1 = new Task("Task_1_1","Test");
        Task task_1_2 = new Task("Task_1_2","Test");

        taskManager.createEpic(epic_1);
        taskManager.createSubTask(subTask_1_1);
        taskManager.createSubTask(subTask_1_2);
        taskManager.createTask(task_1_2);
        taskManager.createTask(task_1_1);

        List<Task> oldHistory = inMemoryHistoryManager.getHistory();
        int oldTasksSize = taskManager.getAllTasks().size() + taskManager.getAllSubTasks().size() +
                taskManager.getAllEpics().size();

        taskManager.deleteTask(task_1_1.getId());

        List<Task> newHistory = inMemoryHistoryManager.getHistory();
        int historySize = inMemoryHistoryManager.getHistory().size();
        int newTasksSize = taskManager.getAllTasks().size() + taskManager.getAllSubTasks().size() +
                taskManager.getAllEpics().size();

        //Проверяем истории
        for(int i = 0;i < historySize;i++){
            assertEquals(oldHistory.get(i),newHistory.get(i));
        }
        //Проверяем количество задач
        assertNotEquals(newTasksSize,oldTasksSize);
    }
}