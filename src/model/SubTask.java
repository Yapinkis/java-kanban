package model;

import java.util.Objects;

public class SubTask extends Task {
    public SubTask() {
        super();
    }

    public SubTask(String name, String description) {
        super(name, description);
    }

    public SubTask(String name) {
        super(name);
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
        return "SubTask {" + "|id=| " + getId() + " |name=| " + getName() + " |status=| " + getStatus() + " |description=| " +
                getDescription() + " |Epic_name| " + getEpic().getName() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return getId() == subTask.getId() &&
                Objects.equals(getName(), subTask.getName()) &&
                Objects.equals(getDescription(), subTask.getDescription()) &&
                Objects.equals(getEpic(), subTask.getEpic());
    }


}
