package com.enonic.xp.impl.scheduler.distributed;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

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

    @Override
    public Instant getValue()
    {
        return value;
    }

    @Override
    public Optional<Instant> nextExecution( final Instant instant )
    {
        return Optional.of( value );
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
            Objects.requireNonNull( value, "value is required" );
        }

        public OneTimeCalendarImpl build()
        {
            validate();
            return new OneTimeCalendarImpl( this );
        }
    }
}
