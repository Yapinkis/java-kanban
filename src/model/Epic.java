package model;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<SubTask> subTasks;

    public Epic() {
        this.tStatus = TasksStatus.EPIC;
        this.subTasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name,description);
        this.subTasks = new ArrayList<>();
        this.tStatus = TasksStatus.EPIC;
    }

    public Epic(String name) {
        super(name);
        this.subTasks = new ArrayList<>();
        this.tStatus = TasksStatus.EPIC;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public void addSubTasks(SubTask subTask) {
        this.subTasks.add(subTask);
    }

    @Override
    public String toString() {
        return "Epic *{" + "Id = " + getId() + " Name = " + getName() + " Status = " + getStatus() + " Description = " +
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
        return Objects.hash(super.hashCode(),subTasks);
    }
}
