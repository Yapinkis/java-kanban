package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    @Override
    public Task createTask(Task task)  {
            super.createTask(task);
            save();
            return task;
    }


    @Override
    public Task createEpic(Epic epic) {
            super.createEpic(epic);
            save();
            return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
            super.createSubTask(subTask);
            save();
            return subTask;
    }

    public void save() throws ManagerSaveException {
        Path path = Paths.get("resources/TasksHistory.csv");
        List<Task> history = historyManager.getHistory();
        Task task = history.get(history.size() - 1);
        try (FileWriter fileWriter = new FileWriter(path.toFile(), StandardCharsets.UTF_8, true)) {
            if (Files.size(path) == 0) {
                fileWriter.write("id,type,name,status,description,epic\n");
            }
            //Возможно в случаях, когда предусмотрена команда очистки истории?
            //Ну либо, когда предусмотрен какой-то общий доступ к файлу и его удаление может привести к ошибке?
            //Наверное это очень ситуативная штука...
            String stringFromTask = makeStringFromTask(task);
            List<String> strings = Files.readAllLines(path);
            if (!strings.contains(stringFromTask)) {
                fileWriter.write(stringFromTask + "\n");
                makeBrowsingHistory(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("При работе с файлом произошла ошибка: " + e.getMessage(), e);
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
            throw new ManagerSaveException("Ошибка при записи истории:");
        }
    }

    private String generateBrowsingHistoryEntry(Task task) {
        StringBuilder wordToTask = new StringBuilder();
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
        try (FileWriter fileWriter = new FileWriter(path.toFile(), StandardCharsets.UTF_8, true)) {
            List<String> tasks = Files.readAllLines(path);
            for (int i = 1; i < tasks.size(); i++) {
                String task = tasks.get(i);
                String[] words = task.split(",");
                TasksStatus status = TasksStatus.valueOf(words[1]);
                if (status.equals(TasksStatus.TASK)) {
                    Task newTask = new Task((Integer.parseInt(words[0])), words[2], words[4]);
                    fileBackedTaskManager.getTasks().put(newTask.getId(), newTask);

                } else if (status.equals(TasksStatus.EPIC)) {
                    Epic newEpic = new Epic(words[2], words[4]);
                    newEpic.setId(Integer.parseInt(words[0]));
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
            throw new ManagerSaveException("При работе с файлом произошла ошибка: " + e.getMessage(), e);
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
