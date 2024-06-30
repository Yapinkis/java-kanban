package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private Integer id = null;
    private String name;
    private String description;
    private TasksStatus tasksStatus;
    protected TasksType tasksType;
    protected Duration duration;
    protected LocalDateTime startTime;
    public static final LocalDateTime defaultTime = LocalDateTime.of(1,1,1,1,1);

    public Task(Integer id, String name, String description, long duration, int year, int month, int day, int hour, int minute) {
        this.id = id;
        this.tasksType = TasksType.TASK;
        this.name = name;
        this.tasksStatus = TasksStatus.NEW;
        this.description = description;
        this.startTime = LocalDateTime.of(year,month,day,hour,minute);
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(String name, String description, long duration, int year, int month, int day, int hour, int minute) {
        this.tasksType = TasksType.TASK;
        this.name = name;
        this.tasksStatus = TasksStatus.NEW;
        this.description = description;
        this.startTime = LocalDateTime.of(year,month,day,hour,minute);
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(String name, String description, long duration, LocalDateTime time) {
        this.tasksType = TasksType.TASK;
        this.name = name;
        this.tasksStatus = TasksStatus.NEW;
        this.description = description;
        this.startTime = time;
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(String name, String description, long duration) {
        this.tasksType = TasksType.TASK;
        this.name = name;
        this.tasksStatus = TasksStatus.NEW;
        this.description = description;
        this.startTime = defaultTime;

        this.duration = Duration.ofMinutes(duration);
    }

    public Task(String name, String description) {
        this.tasksType = TasksType.TASK;
        this.name = name;
        this.tasksStatus = TasksStatus.NEW;
        this.description = description;
        this.startTime = defaultTime;
        this.duration = Duration.ofMinutes(0);
    }

    public Task() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TasksStatus getTasksStatus() {
        return tasksStatus;
    }

    public void setTasksStatus(TasksStatus tasksStatus) {
        this.tasksStatus = tasksStatus;
    }

    public TasksType getTasksType() {
        return tasksType;
    }

    public void setTaskType(TasksType status) {
        this.tasksType = status;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(Task task) {
        this.startTime = task.getStartTime();
    }

    public void setStartTime(LocalDateTime time) {
        this.startTime = time;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(long time) {
        this.duration = Duration.ofMinutes(time);
    }

    @Override
    public String toString() {
        return "Task{" + "Id = " + id + " name = " + name + " status = " + tasksStatus + " description = " +
                description + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getId() == task.getId() &&
                Objects.equals(getName(), task.getName()) &&
                Objects.equals(getDescription(), task.getDescription());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == 0) ? 0 : id);
        result = prime * result + ((id == 0) ? 0 : name.hashCode());
        result = prime * result + (description != null ? description.hashCode() : 0);
        return result;
    }

}
