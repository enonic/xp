package com.enonic.xp.server.udc.impl;

import com.enonic.xp.core.internal.concurrent.RecurringJob;

public interface UdcScheduler
{
    RecurringJob scheduleWithFixedDelay( Runnable command );
}
