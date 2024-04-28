package model;

import java.util.Objects;

public class Task {

    private int id = 1;
    private String name;
    private String description;
    private EnumStatus status;
    protected TasksStatus tStatus;//Посавил protected т.к. такой вариант мне показался более удобным, чем
    //описывать дополнительные геттеры и сеттеры, или я не прав и нужно ыбло всё-таки делать private

    public Task() {
        this.status = EnumStatus.NEW;
    }

    public Task(int id, String name, String description) {
        this.id = id;
        this.tStatus = TasksStatus.TASK;
        this.name = name;
        this.status = EnumStatus.NEW;
        this.description = description;
    }

    public Task(String name, String description) {
        this.tStatus = TasksStatus.TASK;
        this.name = name;
        this.status = EnumStatus.NEW;
        this.description = description;
    }

    public Task(String name) {
        this.tStatus = TasksStatus.TASK;
        this.name = name;
        this.status = EnumStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public EnumStatus getStatus() {
        return status;
    }

    public void setStatus(EnumStatus status) {
        this.status = status;
    }

    public TasksStatus getTaskStatus() {
        return tStatus;
    }

    public void setTaskStatus(TasksStatus status) {
        this.tStatus = status;
    }

    @Override
    public String toString() {
        return "Task{" + "Id = " + id + " name = " + name + " status = " + status + " description = " +
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
        return result;
    }

}
