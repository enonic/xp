package com.enonic.xp.impl.scheduler.distributed;

import java.io.Serializable;

import com.hazelcast.scheduledexecutor.NamedTask;

public interface SchedulableTask
    extends NamedTask, Runnable, Serializable
{
}
