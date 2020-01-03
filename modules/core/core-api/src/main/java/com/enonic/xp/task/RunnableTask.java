package com.enonic.xp.task;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface RunnableTask
{
    void run( TaskId id, ProgressReporter progressReporter );
}
