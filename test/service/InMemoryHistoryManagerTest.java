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
class InMemoryHistoryManagerTest extends TaskManagerTest <InMemoryTaskManager>{

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

    @DisplayName("Проверка обновления head и tail")
    @Test
    void checkingForHeadAndTailUpdates(){
        Task task1 = new Task("Test1","test");
        Task task2 = new Task("Test2","test");
        Task task3 = new Task("Test3","test");

        TaskManager.createTask(task1);
        TaskManager.createTask(task2);
        TaskManager.createTask(task3);


        inMemoryHistoryManager.linkLast(task1);
        inMemoryHistoryManager.linkLast(task2);

        Node head = inMemoryHistoryManager.getHead();
        Node tailBeforeAdd = inMemoryHistoryManager.getTail();

        inMemoryHistoryManager.linkLast(task3);
        Node tailAfterAdd = inMemoryHistoryManager.getTail();

        Node newHead = inMemoryHistoryManager.getHead();

        assertEquals(head, newHead);
        assertNotEquals(tailBeforeAdd, tailAfterAdd);
    }
    @DisplayName("Проверка заполнения узлов списка")
    @Test
    void checkingForNodeUpdates(){

        Task task1 = new Task("Test1","test");
        Task task2 = new Task("Test2","test");
        Task task3 = new Task("Test3","test");
        Task task4 = new Task("Test4","test");

        TaskManager.createTask(task1);
        TaskManager.createTask(task2);
        TaskManager.createTask(task3);
        TaskManager.createTask(task4);

        inMemoryHistoryManager.linkLast(task3);
        inMemoryHistoryManager.linkLast(task2);
        inMemoryHistoryManager.linkLast(task4);
        inMemoryHistoryManager.linkLast(task1);

        Node preNode = inMemoryHistoryManager.getNode(task3);
        Node Node = inMemoryHistoryManager.getNode(task2);
        Node lastNode = inMemoryHistoryManager.getNode(task4);

        assertNotNull(preNode);
        assertNotNull(Node);
        assertNotNull(lastNode);
    }
    @DisplayName("Проверка наличия задачи в менеджере и Id")
    @Test
    void canAddTasksAndHeirsAndCanFindThemById() {

        Epic epic = new Epic("Epic","Test");
        SubTask subTask = new SubTask("SubTask","Test");
        Task task = new Task("Task","Test");

        TaskManager.createEpic(epic);
        TaskManager.createSubTask(subTask);
        TaskManager.createTask(task);

        assertNotNull(TaskManager.getIdEpic(epic.getId()));
        assertNotNull(TaskManager.getIdSubtasks(subTask.getId()));
        assertNotNull(TaskManager.getIdTask(task.getId()));
    }
    @DisplayName("Проверка соответствия по Id")
    @Test
    void canFindById() {

        Epic epic = new Epic("Epic","Test");
        SubTask subTask = new SubTask("SubTask","Test");
        Task task = new Task("Task","Test");

        TaskManager.createEpic(epic);
        TaskManager.createSubTask(subTask);
        TaskManager.createTask(task);

        Epic foundEpic = TaskManager.getIdEpic(epic.getId());
        SubTask foundSubTask = TaskManager.getIdSubtasks(subTask.getId());
        Task foundTask = TaskManager.getIdTask(task.getId());

        assertEquals(epic, foundEpic);
        assertEquals(subTask, foundSubTask);
        assertEquals(task, foundTask);
    }
    @DisplayName("Проверка удаления задач методом remove")
    @Test
    void checkingTheRemovalOfTasksUsingTheRemoveMethod() {
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

        int beforeDel = TaskManager.getHistoryHManager().size();
        Map<Integer, Node> map = inMemoryHistoryManager.getMap();
        inMemoryHistoryManager.remove(epic_1);
        int afterDel = TaskManager.getHistoryHManager().size();
        assertNotEquals(beforeDel, afterDel);
    }
    @DisplayName("Проверка отсутствия дублирования задач и их количества")
    @Test
    void checkingThatThereIsNoDuplicationOfTasks() {
        Epic epic_1 = new Epic("Epic_1","Test");
        SubTask subTask_1_1 = new SubTask("SubTask_1_1","Test");
        SubTask subTask_1_2 = new SubTask("SubTask_1_2","Test");
        Task task_1_1 = new Task("Task_1_1","Test");
        Task task_1_2 = new Task("Task_1_2","Test");

        TaskManager.createEpic(epic_1);
        TaskManager.createSubTask(subTask_1_1);
        TaskManager.createSubTask(subTask_1_2);
        TaskManager.createTask(task_1_1);
        TaskManager.createTask(task_1_2);

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
        int historySize = TaskManager.getHistoryHManager().size();
        List<Task> history = TaskManager.getHistoryHManager();

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

        TaskManager.createEpic(epic_1);
        TaskManager.createSubTask(subTask_1_1);
        TaskManager.createSubTask(subTask_1_2);
        TaskManager.createTask(task_1_1);
        TaskManager.createTask(task_1_2);


        inMemoryHistoryManager.add(epic_1);
        inMemoryHistoryManager.add(subTask_1_1);
        inMemoryHistoryManager.add(subTask_1_2);
        inMemoryHistoryManager.add(task_1_1);
        inMemoryHistoryManager.add(task_1_2);

        List<Task> tasks = TaskManager.getHistoryHManager();

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

        TaskManager.createEpic(epic_1);
        TaskManager.createSubTask(subTask_1_1);
        TaskManager.createSubTask(subTask_1_2);
        TaskManager.createTask(task_1_2);
        TaskManager.createTask(task_1_1);

        inMemoryHistoryManager.add(epic_1);
        inMemoryHistoryManager.add(subTask_1_2);
        inMemoryHistoryManager.add(subTask_1_1);
        inMemoryHistoryManager.add(task_1_1);
        inMemoryHistoryManager.add(task_1_2);

        List<Task> oldHistory = TaskManager.getHistoryHManager();

        inMemoryHistoryManager.add(task_1_2);
        inMemoryHistoryManager.add(task_1_1);
        inMemoryHistoryManager.add(subTask_1_2);
        inMemoryHistoryManager.add(subTask_1_1);
        inMemoryHistoryManager.add(epic_1);

        List<Task> newHistory = TaskManager.getHistoryHManager();
        int historySize = TaskManager.getHistoryHManager().size();

        for(int i = 0;i < historySize;i++){
            assertNotEquals(oldHistory.get(i),newHistory.get(i));
        }
    }
    @DisplayName("После удаления задачи она не остаётся в истории")
    @Test
    void afterDeletingTaskInformationAboutItRemainsInHistory(){
        Epic epic_1 = new Epic("Epic_1","Test");
        SubTask subTask_1_1 = new SubTask("SubTask_1_1","Test");
        SubTask subTask_1_2 = new SubTask("SubTask_1_2","Test");
        Task task_1_1 = new Task("Task_1_1","Test");
        Task task_1_2 = new Task("Task_1_2","Test");

        TaskManager.createEpic(epic_1);
        TaskManager.createSubTask(subTask_1_1);
        TaskManager.createSubTask(subTask_1_2);
        TaskManager.createTask(task_1_2);
        TaskManager.createTask(task_1_1);

        int oldTasksSize = TaskManager.getAllTasks().size() + TaskManager.getAllSubTasks().size() +
                TaskManager.getAllEpics().size();
        TaskManager.deleteTask(task_1_1.getId());
        int newTasksSize = TaskManager.getAllTasks().size() + TaskManager.getAllSubTasks().size() +
                TaskManager.getAllEpics().size();

        //Сравниваем количество задач
        assertNotEquals(newTasksSize,oldTasksSize);
    }


    @DisplayName("Проверка исключений при попытке обратиться к отсутствующей ноде")
    @Test
    void exceptionWhenTryingToAccessWithDeletedNode_NullPointerException() {
        Task task1 = new Task("Test1","forTest");
        Task task2 = new Task("Test2","forTest");
        Task task3 = new Task("Test3","forTest");

        TaskManager.createTask(task1);
        TaskManager.createTask(task2);
        TaskManager.createTask(task3);

        inMemoryHistoryManager.remove(task3);

        assertThrows(NullPointerException.class, () -> {
            inMemoryHistoryManager.remove(task3);
        });
    }
}