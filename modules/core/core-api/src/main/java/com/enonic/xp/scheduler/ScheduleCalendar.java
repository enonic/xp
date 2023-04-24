package com.enonic.xp.scheduler;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ScheduleCalendar
    extends Serializable
{
    Optional<Instant> nextExecution( Instant instant );

    Optional<Duration> nextExecution();

    ScheduleCalendarType getType();
}
