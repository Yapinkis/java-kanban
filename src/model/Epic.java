package model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> idsOfSubTasks;
    //Переписал, теперь у меня экземпляр Эпика хранит только id входящих в него сабтасков
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name,description);
        this.idsOfSubTasks = new ArrayList<>();
        this.tasksType = TasksType.EPIC;
    }

    public List<Integer> getSubTasks() {
        return idsOfSubTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        ArrayList<Integer> someArr = new ArrayList<>();
        int[] arr = subTasks.stream().mapToInt(Task::getId).toArray();
        for (int num : arr) {
            someArr.add(num);
        }
        this.idsOfSubTasks = someArr;
        //Я честно так и не понял, как преобразовать int[] в List<Integer> без forEach
        //И обязательно всё писать в декларативном стиле?
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
