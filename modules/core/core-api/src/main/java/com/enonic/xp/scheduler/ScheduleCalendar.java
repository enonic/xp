package com.enonic.xp.scheduler;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;


public interface ScheduleCalendar
    extends Serializable
{
    Optional<Instant> nextExecution( Instant instant );

    ScheduleCalendarType getType();
}
