package com.enonic.xp.scheduler;

import java.time.Instant;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface OneTimeCalendar
    extends ScheduleCalendar
{
    Instant getValue();
}
