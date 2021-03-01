package com.enonic.xp.impl.scheduler.distributed;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import com.google.common.base.Preconditions;

import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;

public final class OneTimeCalendar
    implements ScheduleCalendar
{
    private static final long serialVersionUID = 0;

    private final Instant value;

    private OneTimeCalendar( final Builder builder )
    {
        this.value = builder.value;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getValue()
    {
        return value.toString();
    }

    @Override
    public Optional<Duration> nextExecution()
    {
        return Optional.of( Duration.between( Instant.now(), value ) );
    }

    @Override
    public ScheduleCalendarType getType()
    {
        return ScheduleCalendarType.ONE_TIME;
    }

    public static class Builder
    {
        private Instant value;

        public Builder value( final Instant value )
        {
            this.value = value;
            return this;
        }

        protected void validate()
        {
            Preconditions.checkNotNull( value, "value must be set." );
        }

        public OneTimeCalendar build()
        {
            validate();
            return new OneTimeCalendar( this );
        }
    }
}
