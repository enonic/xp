package com.enonic.xp.cluster.impl;

import com.enonic.xp.core.internal.concurrent.RecurringJob;

public interface ClusterCheckScheduler
{
    RecurringJob scheduleWithFixedDelay( Runnable command );
}
