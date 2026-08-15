package com.enonic.xp.impl.scheduler.distributed;

import java.time.Instant;
import java.util.Optional;

import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;

import static java.util.Objects.requireNonNull;

public final class OneTimeCalendarImpl
    implements OneTimeCalendar
{
    private static final long serialVersionUID = 0;

    private final Instant value;

    private final boolean deleteAfterRun;

    private OneTimeCalendarImpl( final Builder builder )
    {
        this.value = builder.value;
        this.deleteAfterRun = builder.deleteAfterRun;
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
    public boolean isDeleteAfterRun()
    {
        return deleteAfterRun;
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

        private boolean deleteAfterRun;

        public Builder value( final Instant value )
        {
            this.value = value;
            return this;
        }

        public Builder deleteAfterRun( final boolean deleteAfterRun )
        {
            this.deleteAfterRun = deleteAfterRun;
            return this;
        }

        protected void validate()
        {
            requireNonNull( value, "value is required" );
        }

        public OneTimeCalendarImpl build()
        {
            validate();
            return new OneTimeCalendarImpl( this );
        }
    }
}
