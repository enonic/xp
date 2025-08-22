package com.enonic.xp.impl.scheduler.distributed;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;

public final class CronCalendarImpl
    implements CronCalendar
{
    private static final long serialVersionUID = 0;

    private static final CronDefinition DEFINITION = CronDefinitionBuilder.instanceDefinitionFor( CronType.UNIX );

    private static final CronParser PARSER = new CronParser( DEFINITION );

    private final Cron cron;

    private final TimeZone timeZone;

    private final ExecutionTime executionTime;

    private CronCalendarImpl( final Builder builder )
    {
        this.timeZone = builder.timeZone;
        this.cron = PARSER.parse( builder.value );
        this.executionTime = ExecutionTime.forCron( this.cron );
    }

    public static boolean isCronValue( final String value )
    {
        try
        {
            PARSER.parse( value );
            return true;
        }
        catch ( IllegalArgumentException e )
        {
            return false;
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Optional<Instant> nextExecution( final Instant instant )
    {
        return this.executionTime.nextExecution( ZonedDateTime.ofInstant( instant, timeZone.toZoneId() ) ).map( ZonedDateTime::toInstant );
    }

    @Override
    public ScheduleCalendarType getType()
    {
        return ScheduleCalendarType.CRON;
    }

    @Override
    public String getCronValue()
    {
        return cron.asString();
    }

    @Override
    public TimeZone getTimeZone()
    {
        return timeZone;
    }

    private Object writeReplace()
    {
        return new SerializedForm( this );
    }

    private static class SerializedForm
        implements Serializable
    {
        private static final long serialVersionUID = 0;

        private final String value;

        private final String timezone;

        SerializedForm( final CronCalendarImpl calendar )
        {
            this.value = calendar.getCronValue();
            this.timezone = calendar.getTimeZone().getID();
        }

        private Object readResolve()
        {
            return CronCalendarImpl.create().
                value( value ).
                timeZone( TimeZone.getTimeZone( timezone ) ).
                build();
        }
    }

    public static class Builder
    {
        private String value;

        private TimeZone timeZone;

        public Builder timeZone( final TimeZone timeZone )
        {
            this.timeZone = timeZone;
            return this;
        }

        public Builder value( final String value )
        {
            this.value = value;
            return this;
        }

        protected void validate()
        {
            Objects.requireNonNull( value, "value is required" );
            Objects.requireNonNull( timeZone, "timeZone is required" );
        }

        public CronCalendarImpl build()
        {
            validate();
            return new CronCalendarImpl( this );
        }
    }
}
