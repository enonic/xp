package com.enonic.xp.scheduler;

import java.time.Instant;


public interface OneTimeCalendar
    extends ScheduleCalendar
{
    Instant getValue();
}
