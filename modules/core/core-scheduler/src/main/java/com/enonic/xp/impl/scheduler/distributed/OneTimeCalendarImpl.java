package com.enonic.xp.impl.scheduler.distributed;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import com.google.common.base.Preconditions;

import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;

public final class OneTimeCalendarImpl
    implements OneTimeCalendar
{
    private static final long serialVersionUID = 0;

    private final Instant value;

    private OneTimeCalendarImpl( final Builder builder )
    {
        this.value = builder.value;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Instant getValue()
    {
        return value;
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

        public OneTimeCalendarImpl build()
        {
            validate();
            return new OneTimeCalendarImpl( this );
        }
    }
}
