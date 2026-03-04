package com.enonic.xp.task;

public interface RunnableTask
{
    void run( TaskId id, ProgressReporter progressReporter );
}
