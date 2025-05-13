package com.enonic.xp.inputtype;

import java.time.Duration;
import java.time.Period;

final class RelativeTime
{
    private final Duration duration;

    private final Period period;

    RelativeTime( Duration duration, Period period )
    {
        this.duration = duration;
        this.period = period;
    }

    public Duration getTime()
    {
        return duration;
    }

    public Period getDate()
    {
        return period;
    }
}
