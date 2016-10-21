package com.enonic.xp.task;

import com.google.common.annotations.Beta;

@Beta
public interface RunnableTask
{
    void run( TaskId id, ProgressReporter progressReporter );
}
