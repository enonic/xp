package com.enonic.xp.scheduler;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ScheduleCalendar
    extends Serializable
{
    Optional<Duration> timeToNextExecution();

    Optional<ZonedDateTime> nextExecution( Instant instant );

    ScheduleCalendarType getType();
}
