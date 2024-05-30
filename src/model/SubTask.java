package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    public SubTask(String name, String description, int duration, int year, int month, int day, int hour, int minute) {
        super(name, description);
        this.tasksType = TasksType.SUBTASK;
        this.startTime = LocalDateTime.of(year,month,day,hour,minute);
        this.duration = Duration.ofMinutes(duration);
    }

    public SubTask(String name, String description, LocalDateTime time) {
        super(name, description);
        this.tasksType = TasksType.SUBTASK;
        this.startTime = time;
        this.duration = Duration.ofMinutes(0);
    }

    public SubTask(String name, String description, int duration, LocalDateTime time) {
        super(name, description);
        this.tasksType = TasksType.SUBTASK;
        this.startTime = time;
        this.duration = Duration.ofMinutes(duration);
    }

    public SubTask(String name, String description, int duration) {
        super(name, description);
        this.tasksType = TasksType.SUBTASK;
        this.startTime = Task.defaultTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public SubTask(String name, String description) {
        super(name, description);
        this.tasksType = TasksType.SUBTASK;
        this.startTime = Task.defaultTime;
        this.duration = Duration.ofMinutes(0);
    }

    private Epic epic;

    public Epic getEpic() {
       return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "SubTask {" + "|id=| " + getId() + " |name=| " + getName() + " |status=| " + getTasksStatus() + " |description=| " +
                getDescription() + " |Epic_name| " + getEpic().getName() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return getId() == subTask.getId() &&
                Objects.equals(getName(), subTask.getName()) &&
                Objects.equals(getDescription(), subTask.getDescription()) &&
                Objects.equals(getEpic(), subTask.getEpic());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),epic);
    }


}
