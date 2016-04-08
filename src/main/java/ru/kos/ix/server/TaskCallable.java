package ru.kos.ix.server;

import ru.kos.ix.dto.AnsTask;
import ru.kos.ix.dto.Task;

import java.util.concurrent.Callable;

/**
 * Created by Константин on 08.04.2016.
 */
public class TaskCallable implements Callable<AnsTask> {

    private Task task;

    public TaskCallable(Task task) {
        this.task = task;
    }

    @Override
    public AnsTask call() throws Exception {
        Thread.sleep(2000);
        return new AnsTask(task.getId(), "Hello");
    }
}
