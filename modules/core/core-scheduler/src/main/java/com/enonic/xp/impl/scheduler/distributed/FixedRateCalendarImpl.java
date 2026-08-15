package com.enonic.xp.impl.scheduler.distributed;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import com.enonic.xp.scheduler.FixedRateCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;

import static java.util.Objects.requireNonNull;

public final class FixedRateCalendarImpl
    implements FixedRateCalendar
{
    private static final long serialVersionUID = 0;

    private final Duration duration;

    private FixedRateCalendarImpl( final Builder builder )
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
        return ScheduleCalendarType.FIXED_RATE;
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

        public FixedRateCalendarImpl build()
        {
            validate();
            return new FixedRateCalendarImpl( this );
        }
    }
}
