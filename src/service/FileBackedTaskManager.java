package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    static final Path path = Paths.get("resources/TasksHistory.csv");
    ArrayList<Integer> calc = new ArrayList<>();

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
    public Epic createEpic(Epic epic) {
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
        List<Task> history = historyManager.getHistory();
        try (FileWriter fileWriter = new FileWriter(path.toFile(), StandardCharsets.UTF_8, true)) {
            Files.write(path, Collections.emptyList());
                if (Files.size(path) == 0) {
                    fileWriter.write("id,type,name,status,description,epic,startTime,duration\n\n");
                }
                for (Task task : history) {
                    String stringFromTask = makeStringFromTask(task);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (task.getTasksType() == TasksType.EPIC) {
            if (task.getStartTime().equals(LocalDateTime.of(1,1,1,1,1))) {
                makeString.append(String.join(",", String.valueOf(task.getId()), task.getTasksType().toString(),
                        task.getName(), task.getTasksStatus().toString(), task.getDescription(), epic.getName() + "."))
                        .append("\nДата начала первой подазачи Эпика не была определена.")
                        .append("\nПродолжительность Эпика: ").append(task.getDuration().toMinutes()).append(" минут.").append("\n");
            } else {
                makeString.append(String.join(",", String.valueOf(task.getId()), task.getTasksType().toString(),
                        task.getName(), task.getTasksStatus().toString(), task.getDescription(),
                        String.valueOf(epic.getName())," \nДата начала первой подазачи Эпика: " + task.getStartTime().format(formatter),
                        "\nПродолжительность Эпика: " + task.getDuration().toMinutes() + " минут")).append("\n");
            }
        } else {
            if (task.getStartTime().equals(LocalDateTime.of(1,1,1,1,1))) {
                makeString.append(String.join(",", String.valueOf(task.getId()), task.getTasksType().toString(),
                        task.getName(), task.getTasksStatus().toString(), task.getDescription()))
                        .append("\nДата начала задачи не была указана.").append("\nПродолжительность задачи: ")
                        .append(task.getDuration().toMinutes()).append(" минут.").append("\n");
            } else {
                makeString.append(String.join(",", String.valueOf(task.getId()), task.getTasksType()
                        .toString(), task.getName(), task.getTasksStatus().toString(), task.getDescription()))
                        .append("\n").append(task.getStartTime().format(formatter))
                        .append("\nПродолжительность задачи: ").append(task.getDuration().toMinutes()).append(" минут").append("\n");
            }
        }
        return makeString.toString();
    }

    public void makeBrowsingHistory(Task task) throws ManagerSaveException {
        Path path = Paths.get("resources/BrowsingHistory.csv");
        String historyModule = generateBrowsingHistoryEntry(task);
        boolean flag;
        try (FileWriter fileWriter = new FileWriter(path.toFile(), StandardCharsets.UTF_8, true)) {
            List<String> strings = Files.readAllLines(path, StandardCharsets.UTF_8);
            flag = strings.stream().anyMatch(bool -> bool.equals(historyModule));
            if (!flag) {
                if (calc.stream().anyMatch(bool -> bool.equals(task.getId()))) {
                    fileWriter.write(historyModule + "      <--была отредактирована. исх. ID: " + task.getId() + "\n\n");
                } else {
                    fileWriter.write(historyModule + "\n\n");
                }
            }
            calc.add(task.getId());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи истории:");
        }
    }

    private String generateBrowsingHistoryEntry(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder wordToTask = new StringBuilder();
        wordToTask.append("ID:" + " " + task.getId()).append(" " + task.getTasksType()).append(" ").append(task.getName()).append(" ");
        if (task.getTasksStatus() == TasksStatus.NEW) {
            wordToTask.append("осталась в состоянии: ").append(task.getTasksStatus()).append(" и не была просмотрена.")
                    .append(" Время старта задачи: "). append(task.getStartTime().format(formatter)).append(".").append(" Продолжительность: ")
                    .append(task.getDuration().toMinutes()).append(" мин.");
        } else if (task.getTasksStatus() == TasksStatus.IN_PROGRESS) {
            wordToTask.append("обновила статус ").append(task.getTasksStatus()).append(" и была принята в работу.");
        } else {
            wordToTask.append("имеет статус ").append(task.getTasksStatus()).append(" и был завершен!");
        }
        return wordToTask.toString();
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getHistoryManager());
        if (!file.exists()) {
            throw new ManagerSaveException("Файл не найден");
        }
        try (FileWriter fileWriter = new FileWriter(path.toFile(), StandardCharsets.UTF_8, true)) {
            List<String> tasks = Files.readAllLines(path);
            for (int i = 2; i < tasks.size(); i += 4) {
                String task = tasks.get(i);
                String[] words = task.split(",");
                if (words[0].matches("\\d+")) {
                    TasksType status = TasksType.valueOf(words[1]);
                    if (status.equals(TasksType.TASK)) {
                        Task newTask = new Task(words[2], words[4]);
                        newTask.setId(Integer.parseInt(words[0]));
                        fileBackedTaskManager.getTasks().put(newTask.getId(), newTask);
                        fileBackedTaskManager.getIdTask(newTask.getId());
                    } else if (status.equals(TasksType.EPIC)) {
                        Epic newEpic = new Epic(words[2], words[4]);
                        newEpic.setId(Integer.parseInt(words[0]));
                        fileBackedTaskManager.getEpics().put(newEpic.getId(), newEpic);
                        fileBackedTaskManager.getIdEpic(newEpic.getId());
                    } else {
                        int point = fileBackedTaskManager.backLastEpic();
                        Epic epic = fileBackedTaskManager.getIdEpic(point);
                        SubTask newSubTask = new SubTask(words[2], words[4]);
                        newSubTask.setEpic(epic.getId());
                        newSubTask.setId(Integer.parseInt(words[0]));
                        epic.addSubTasks(newSubTask);
                        fileBackedTaskManager.getSubTasks().put(newSubTask.getId(), newSubTask);
                        fileBackedTaskManager.getIdSubtasks(newSubTask.getId());
                    }
                } else {
                    break;
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("При работе с файлом произошла ошибка: " + e.getMessage(), e);
        }
        return fileBackedTaskManager;
    }

    public void clear() {
        try {
            Files.write(path, Collections.emptyList());
        } catch (IOException e) {
            throw new ManagerSaveException("При работе с файлом произошла ошибка: " + e.getMessage(), e);
        }
    }

    public int backLastEpic() {
        return getAllEpics().stream().mapToInt(Task::getId).max().orElse(0);
    }

    public boolean equals(Task task) {
        boolean flag = false;
        for (int i = 0; i < historyManager.getHistory().size();i++) {
            if (historyManager.getHistory().get(i).equals(task)) {
                flag = true;
            }
        }
        return flag;
    }
}
