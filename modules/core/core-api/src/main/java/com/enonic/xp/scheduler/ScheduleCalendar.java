package com.enonic.xp.scheduler;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ScheduleCalendar
    extends Serializable
{
    Optional<Duration> nextExecution();
}
