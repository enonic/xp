package com.enonic.xp.impl.task;

import com.enonic.xp.core.internal.concurrent.RecurringJob;

public interface TaskManagerCleanupScheduler
{
    RecurringJob scheduleWithFixedDelay( Runnable command );
}
