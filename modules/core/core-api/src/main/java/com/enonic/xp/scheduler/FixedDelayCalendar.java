package com.enonic.xp.scheduler;

import java.time.Duration;


public interface FixedDelayCalendar
    extends ScheduleCalendar
{
    Duration getDuration();
}
