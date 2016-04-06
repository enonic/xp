package com.enonic.xp.inputtype;

import java.time.Duration;
import java.time.Period;

public class RelativeTime
{

    private Duration duration;

    private Period period;

    RelativeTime( Duration duration, Period period ) {
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