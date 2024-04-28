package service;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    @Override
    public Task createTask(Task task)  {
        try {
            super.createTask(task);
            save();
        } catch (ManagerSaveException e) {
            System.err.println("Возникла ошибка в методе при записи задачи: " + e.getMessage());
        }
        return task;
    } //Я не могу обработать исключение так,как это показано в ТЗ(без блока try\catch).Возникает ошибка:
    //Unhandled exception: model.ManagerSaveException. А по условиям, нам нужно создать пользовательское исключение
    //Возможно проще наследовать исключение сразу для всего класса?

    @Override
    public Task createEpic(Epic epic) {
        try {
            super.createEpic(epic);
            save();
        } catch (ManagerSaveException e) {
            System.err.println("Возникла ошибка в методе при записи эпика: " + e.getMessage());
        }
        return null;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        try {
            super.createSubTask(subTask);
            save();
        } catch (ManagerSaveException e) {
            System.err.println("Возникла ошибка в методе при записи подзадачи: " + e.getMessage());
        }
        return subTask;
    }

    public void save() throws ManagerSaveException {
        Path path = Paths.get("resources/TasksHistory.csv");
        List<Task> history = historyManager.getHistory();
        Task task = history.get(history.size() - 1);
        try (FileWriter fileWriter = new FileWriter(path.toFile(), StandardCharsets.UTF_8, true)) {
            if (Files.size(path) == 0) {
                fileWriter.write("id,type,name,status,description,epic\n");
            } //Нормально и проводить проверку на вес файла? Просто теоретически, если он пустой,
            //а заголовок это всегда первая строка, то именно его сперва мы и должны записать
            String stringFromTask = makeStringFromTask(task);
            List<String> strings = Files.readAllLines(path);
            if (!strings.contains(stringFromTask)) {
                fileWriter.write(stringFromTask + "\n");
                makeBrowsingHistory(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("При работе с файлом произошла ошибка: ", e);
        }
    }
    //Метод для перобразования задачи в строку

    public String makeStringFromTask(Task task) {
        StringBuilder makeString = new StringBuilder();
        Epic epic = getEpics().get(backLastEpic());
        if (task.getTaskStatus() == TasksStatus.EPIC) {
            makeString.append(String.join(",", String.valueOf(task.getId()), task.getTaskStatus().toString(),
                    task.getName(), task.getStatus().toString(), task.getDescription(),
                    String.valueOf(epic.getName())));
        } else {
            makeString.append(String.join(",", String.valueOf(task.getId()), task.getTaskStatus().toString(),
                    task.getName(), task.getStatus().toString(), task.getDescription()));
        }
        return makeString.toString();
    }
//    public void makeBrowsingHistory(Task task) throws ManagerSaveException {
//        Path path = Paths.get("resources/BrowsingHistory.csv");
//        try (FileWriter fileWriter = new FileWriter(path.toFile(), StandardCharsets.UTF_8,true)) {
//            if (task.getStatus().equals(EnumStatus.NEW)) {
//                switch (task.getTaskStatus()) {
//                    case TASK:
//                    case SUBTASK:
//                        fileWriter.write(task.getTaskStatus() + " " + task.getName() + " осталась в состоянии: " +
//                                task.getStatus() + " и не была просмотрена." + "\n");
//                        break;
//                    case EPIC:
//                        Epic epic = getIdEpic(task.getId());
//                        List<SubTask> subs = epic.getSubTasks();
//                        fileWriter.write(task.getTaskStatus() + " " + task.getName() + " имеет статус " +
//                                task.getStatus() + " и не был просомтрен." + "\n");
//                        for (Task subTask : subs) {
//                            fileWriter.write("     " + subTask.getTaskStatus() + " " + subTask.getName()
//                                    + subTask.getStatus() + " не была просомтрена." + "\n");
//                        }
//                        break;
//                }
//            } else if (task.getStatus().equals(EnumStatus.IN_PROGRESS)) {
//                switch (task.getTaskStatus()) {
//                    case TASK:
//                    case SUBTASK:
//                        fileWriter.write(task.getTaskStatus() + " " + task.getName() + " обновила статус " +
//                                task.getStatus() + " и была принята в работу." + "\n");
//                        break;
//                    case EPIC:
//                        Epic epic = getIdEpic(task.getId());
//                        List<SubTask> subs = epic.getSubTasks();
//                        fileWriter.write(task.getTaskStatus() + " " + task.getName() + " имеет статус " +
//                                task.getStatus() + " была просомтрена." + "\n");
//                        for (Task subTask : subs) {
//                            fileWriter.write("     " + subTask.getTaskStatus() + " " + subTask.getName()
//                                    + subTask.getStatus() + " была принята в работу." + "\n");
//                        }
//                        break;
//                }
//            } else {
//                switch (task.getTaskStatus()) {
//                    case TASK:
//                    case SUBTASK:
//                        fileWriter.write(task.getTaskStatus() + " " + task.getName() + " " +
//                                task.getStatus() + " выполнена." + "\n");
//                        break;
//                    case EPIC:
//                        Epic epic = getIdEpic(task.getId());
//                        List<SubTask> subs = epic.getSubTasks();
//                        fileWriter.write(task.getTaskStatus() + " " + task.getName() + " " +
//                                task.getStatus() + " завершён." + "\n");
//                        for (Task subTask : subs) {
//                            fileWriter.write("     " + subTask.getTaskStatus() + " " + subTask.getName()
//                                    + subTask.getStatus() + " выполнена." + "\n");
//                        }
//                        break;
//                }
//            }
//        } catch (IOException e) {
//            throw new ManagerSaveException("При записи истории произошла ошибка:", e);
//        }
//    } //Наверное, это была не самая хорошая реализация метода?
    //Я так понимаю, такие огромные куски кода лучше разбивать на подметоды?

    public void makeBrowsingHistory(Task task) throws ManagerSaveException {
        Path path = Paths.get("resources/BrowsingHistory.csv");
        String historyModule = generateBrowsingHistoryEntry(task);
        boolean flag = false;
        try (FileWriter fileWriter = new FileWriter(path.toFile(), StandardCharsets.UTF_8, true)) {
            List<String> strings = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : strings) {
                if (line.equals(historyModule)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                fileWriter.write(historyModule + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи истории:", e);
        }
    }

    private String generateBrowsingHistoryEntry(Task task) {
        StringBuilder wordToTask = new StringBuilder();//Нормально же, что я везде использую StringBuilder?
        wordToTask.append(task.getTaskStatus()).append(" ").append(task.getName()).append(" ");
        if (task.getStatus() == EnumStatus.NEW) {
            wordToTask.append("осталась в состоянии: ").append(task.getStatus()).append(" и не была просмотрена.");
        } else if (task.getStatus() == EnumStatus.IN_PROGRESS) {
            wordToTask.append("обновила статус ").append(task.getStatus()).append(" и была принята в работу.");
        } else {
            wordToTask.append("имеет статус ").append(task.getStatus()).append(" и был завершен!");
        }
        return wordToTask.toString();
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getHistoryManager());
        Path path = Paths.get("resources/TasksHistory.csv");
        ArrayList<String> tasks = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8))) {
            //Я же в теории могу обойтись без BufferedReader и использовать только Files.readAllLines для чтения
            //из файла? Или здесь есть какие-то ограничения по памяти?
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                tasks.add(line);
            }
            for (int i = 1; i < tasks.size(); i++) {
                String task = tasks.get(i);
                String[] words = task.split(",");
                TasksStatus status = TasksStatus.valueOf(words[1]);
                if (status.equals(TasksStatus.TASK)) {
                    Task newTask = new Task((Integer.parseInt(words[0])), words[2], words[4]);
                    //newTask.setTaskStatus(TasksStatus.TASK);
                    fileBackedTaskManager.getTasks().put(newTask.getId(), newTask);

                } else if (status.equals(TasksStatus.EPIC)) {
                    Epic newEpic = new Epic(words[2], words[4]);
                    newEpic.setId(Integer.parseInt(words[0]));
                    //newEpic.setTaskStatus(TasksStatus.EPIC);
                    fileBackedTaskManager.getEpics().put(newEpic.getId(), newEpic);
                } else {
                    int point = 0;
                    for (Epic epic : fileBackedTaskManager.getAllEpics()) {
                        if (epic.getId() > point) {
                            point = epic.getId();
                        }
                    }
                    Epic epic = fileBackedTaskManager.getIdEpic(point);
                    SubTask newSubTask = new SubTask(words[2],words[4]);
                    newSubTask.setEpic(epic);
                    newSubTask.setId(Integer.parseInt(words[0]));
                    epic.addSubTasks(newSubTask);
                    fileBackedTaskManager.getSubTasks().put(newSubTask.getId(), newSubTask);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("При работе с файлом произошла ошибка: ", e);
        }
        return fileBackedTaskManager;
    }

    public int backLastEpic() {
        int point = 0;
        for (Epic epic : getAllEpics()) {
            if (epic.getId() > point) {
                point = epic.getId();
            }
        }
        return point;
    }
}
