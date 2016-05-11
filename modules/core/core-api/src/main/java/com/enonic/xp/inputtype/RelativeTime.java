package com.enonic.xp.inputtype;

import java.time.Duration;
import java.time.Period;

import com.google.common.annotations.Beta;

@Beta
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