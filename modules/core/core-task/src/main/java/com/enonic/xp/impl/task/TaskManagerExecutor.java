package com.enonic.xp.impl.task;

import com.enonic.xp.app.ApplicationKey;

public interface TaskManagerExecutor
{
    /**
     * Executes a task command on the executor dedicated to the given application.
     * The application's executor is shut down when the application stops, interrupting its running tasks.
     *
     * @param applicationKey application the task belongs to
     * @param command        task command
     * @throws java.util.concurrent.RejectedExecutionException if the executor is already shut down
     */
    void execute( ApplicationKey applicationKey, Runnable command );
}
