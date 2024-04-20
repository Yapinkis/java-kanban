package model;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<SubTask> subTasks;

    public Epic() {
        this.subTasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name,description);
        this.subTasks = new ArrayList<>();
    }

    public Epic(String name) {
        super(name);
        this.subTasks = new ArrayList<>();
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
        Epic epic = (Epic) o;
        return getId() == epic.getId() &&
                Objects.equals(getName(), epic.getName()) &&
                Objects.equals(getDescription(), epic.getDescription()) &&
                Objects.equals(getSubTasks(), epic.getSubTasks());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 20;
        result = prime * result + ((getId() == 0) ? 0 : getId());
        result = prime * result + ((getId() == 0) ? 0 : getName().hashCode());
        return result;
    }
}
