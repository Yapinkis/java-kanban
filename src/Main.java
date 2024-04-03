import model.EnumStatus;
import model.Epic;
import model.SubTask;
import model.Task;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

        Task task_1 = taskManager.createTask(new Task("Приготовить завтрак","Пожарить яичницу"));
        Task task_2 = taskManager.createTask(new Task("Сходить в магазин", "Купить продуктов"));

        Epic epic_1 = taskManager.createEpic(new Epic("Уборка","Убраться в квартире"));
        SubTask subTask1_1 = taskManager.createSubTask(new SubTask("Влажная уборка","Помыть пол"));
        SubTask subTask1_2 = taskManager.createSubTask(new SubTask("Убрать мусор", "Собрать и вынести" +
                "мусор из квартиры"));

        Epic epic_2 = taskManager.createEpic(new Epic("Тренировка","Заняться спортом"));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Кардио","Ходьба на беговой дорожке"));

        System.out.println(task_1);
        System.out.println(task_2);
        System.out.println(epic_1);
        System.out.println(taskManager.showEpicSubtasks(epic_1));
        System.out.println(epic_2);
        System.out.println(taskManager.showEpicSubtasks(epic_2));

        task_1.setStatus(EnumStatus.IN_PROGRESS);
        task_2.setStatus(EnumStatus.IN_PROGRESS);
        taskManager.updateTask(task_1);
        taskManager.updateTask(task_2);
        subTask1_1.setStatus(EnumStatus.IN_PROGRESS);
        subTask1_2.setStatus(EnumStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask1_1);
        taskManager.updateSubTask(subTask1_2);
        subTask2.setStatus(EnumStatus.DONE);
        taskManager.updateSubTask(subTask2);

        taskManager.deleteTask(task_1.getId());
        taskManager.deleteEpic(epic_1.getId());
        taskManager.updateSubTask(subTask1_1);

        System.out.println();
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.showEpicSubtasks(epic_2));

    }
}
