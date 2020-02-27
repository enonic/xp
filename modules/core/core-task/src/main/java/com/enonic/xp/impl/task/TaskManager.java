package com.enonic.xp.impl.task;

import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public interface TaskManager
    extends TaskInfoManager
{
    TaskId submitTask( RunnableTask runnable, String description, String name );
}
