package service;

import org.junit.Before;

abstract class TaskManagerTest <T extends TaskManager> {
    protected T TaskManager;
    protected abstract T createTaskManager();

    @Before
    public void setUp() {
        TaskManager = createTaskManager();
    }
}
