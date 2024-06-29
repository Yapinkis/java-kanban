
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
//        manager.createTask(new Task("Task1","forTest"));
//        manager.createTask(new Task("Task2","forTest"));
//        manager.createTask(new Task("Task3","forTest"));
//
//        Task task1 = new Task("Task4","forTest1");
//        Task task2 = new Task("Task5","forTest1");
//        Task task3 = new Task("Task6","forTest1");
//
//        manager.createTask(task1);
//        manager.createTask(task2);
//        manager.createTask(task3);
//        task1.setName("Neeeew");
//        manager.updateTask(task1);
//        manager.deleteTask(task3.getId());
//        manager.clearAllTasks();
//        manager.createEpic(new Epic("new","Subs"));
//        manager.createSubTask(new SubTask("new111","Subs111"));
//        manager.createSubTask(new SubTask("new111","Subs111"));
//        manager.createSubTask(new SubTask("new111","Subs111"));
//        manager.createSubTask(new SubTask("new111","Subs111"));
//        manager.clearAllSubTasks();
//        manager.createEpic(new Epic("new","Subs"));
//        SubTask subTask = new SubTask("subTask","subTaskForTest1");
//        manager.createSubTask(subTask);
//        manager.deleteSubTask(subTask.getId());
//        manager.createEpic(new Epic("Epic3","forrrrrr"));
//        manager.createSubTask(new SubTask("new111","Subs111"));
//        manager.createSubTask(new SubTask("new111","Subs111"));
//        manager.createSubTask(new SubTask("new111","Subs111"));
//        manager.createSubTask(new SubTask("new111","Subs111"));
//        manager.clearAllEpics();
//
//        Epic epic = new Epic("Epic","one");
//        manager.createEpic(epic);
//        manager.createSubTask(new SubTask("1","Subs111"));
//        manager.createSubTask(new SubTask("2","Subs111"));
//        manager.createSubTask(new SubTask("3","Subs111"));
//        manager.createSubTask(new SubTask("4","Subs111"));
//
//        manager.deleteEpic(epic.getId());
//
//        Set<Task> other = new TreeSet<>(manager.getPrioritizedTasks());
//
//        manager.clearAllEpics();
//        manager.clearAllTasks();
//
//        Task task12 = new Task("Task4","forTest1");
//        Task task22 = new Task("Task5","forTest1");
//
//        manager.createTask(task12);
//        manager.createTask(task22);

        Epic epic1 = new Epic("1","tesr1");
        Task task1 = new Task("new","test1");
        Task task2 = new Task("new","test1");
        Task task3 = new Task("new","test1");
        Epic epic2 = new Epic("2","tesr1");
        SubTask subTask = new SubTask("Othe","hose");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epic1);
        manager.createTask(epic2);
        manager.createTask(subTask);

        manager.clearAllTasks();
        manager.clearAllEpics();
        manager.clearAllEpics();

        manager.createTask(task1);
        task1.setName("Newцццццййцй");
        manager.updateTask(task1);
        manager.createEpic(epic2);
        subTask.setEpic(epic2.getId());
        manager.updateSubTask(subTask);
        System.out.println();

    }
}
