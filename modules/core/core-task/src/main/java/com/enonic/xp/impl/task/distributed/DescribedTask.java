package com.enonic.xp.impl.task.distributed;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

public interface DescribedTask
{
    TaskId getTaskId();

    String getDescription();

    String getName();

    TaskContext getTaskContext();

    ApplicationKey getApplicationKey();

    void run( ProgressReporter progressReporter );
}
