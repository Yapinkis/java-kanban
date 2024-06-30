package model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> idsOfSubTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name,description);
        this.idsOfSubTasks = new ArrayList<>();
        this.tasksType = TasksType.EPIC;
    }

    public Epic(Integer id, String name, String description) {
        super(name,description,id);
        this.idsOfSubTasks = new ArrayList<>();
        this.tasksType = TasksType.EPIC;
    }


    public List<Integer> getSubTasks() {
        return idsOfSubTasks;
    }

    public void setSubTasks(List<Integer> subTasks) {
        this.idsOfSubTasks = new ArrayList<>(subTasks);
    }

    public void addSubTasks(SubTask subTask) {
        this.idsOfSubTasks.add(subTask.getId());
    }

    public void setEndTime(Task task) {
        this.endTime = task.getEndTime();
    }

    public LocalDateTime getEpicEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic *{" + "Id = " + getId() + " Name = " + getName() + " Status = " + getTasksStatus() + " Description = " +
                getDescription() + " }*";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return getId() == epic.getId() &&
                Objects.equals(getName(), epic.getName()) &&
                Objects.equals(getDescription(), epic.getDescription()) &&
                Objects.equals(getSubTasks(), epic.getSubTasks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),idsOfSubTasks);
    }
}
