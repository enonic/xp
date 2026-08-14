package com.enonic.xp.impl.scheduler.distributed;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import com.enonic.xp.scheduler.FixedDelayCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;

import static java.util.Objects.requireNonNull;

public final class FixedDelayCalendarImpl
    implements FixedDelayCalendar
{
    private static final long serialVersionUID = 0;

    private final Duration duration;

    private FixedDelayCalendarImpl( final Builder builder )
    {
        this.duration = builder.duration;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Duration getDuration()
    {
        return duration;
    }

    @Override
    public Optional<Instant> nextExecution( final Instant instant )
    {
        return Optional.of( instant.plus( duration ) );
    }

    @Override
    public ScheduleCalendarType getType()
    {
        return ScheduleCalendarType.FIXED_DELAY;
    }

    public static class Builder
    {
        private Duration duration;

        public Builder duration( final Duration duration )
        {
            this.duration = duration;
            return this;
        }

        protected void validate()
        {
            requireNonNull( duration, "duration is required" );
            if ( duration.isNegative() || duration.isZero() )
            {
                throw new IllegalArgumentException( "duration must be positive" );
            }
        }

        public FixedDelayCalendarImpl build()
        {
            validate();
            return new FixedDelayCalendarImpl( this );
        }
    }
}
