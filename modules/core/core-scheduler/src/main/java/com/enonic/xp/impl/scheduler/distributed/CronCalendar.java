package com.enonic.xp.impl.scheduler.distributed;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.common.base.Preconditions;

import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;

public final class CronCalendar
    implements ScheduleCalendar
{
    private static final long serialVersionUID = 0;

    private static final CronDefinition DEFINITION = CronDefinitionBuilder.instanceDefinitionFor( CronType.UNIX );

    private static final CronParser PARSER = new CronParser( DEFINITION );

    private static final CronDescriptor DESCRIPTOR =
        new CronDescriptor( ResourceBundle.getBundle( "properties/CronUtilsI18N", Locale.UK ) );

    private final Cron cron;

    private final TimeZone timeZone;

    private final ExecutionTime executionTime;

    private final String description;

    private CronCalendar( final Builder builder )
    {
        this.timeZone = builder.timeZone;
        this.cron = PARSER.parse( builder.value );
        this.executionTime = ExecutionTime.forCron( this.cron );
        this.description = DESCRIPTOR.describe( this.cron );
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
    public Optional<Duration> nextExecution()
    {
        return this.executionTime.timeToNextExecution( ZonedDateTime.now( timeZone.toZoneId() ) );
    }

    @Override
    public ScheduleCalendarType getType()
    {
        return ScheduleCalendarType.CRON;
    }

    public String getDescription()
    {
        return description;
    }

    public String getCronValue()
    {
        return cron.asString();
    }

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

        SerializedForm( final CronCalendar calendar )
        {
            this.value = calendar.getCronValue();
            this.timezone = calendar.getTimeZone().getID();
        }

        private Object readResolve()
        {
            return CronCalendar.create().
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
            Preconditions.checkNotNull( value, "value must be set." );
            Preconditions.checkNotNull( timeZone, "timeZone must be set." );
        }

        public CronCalendar build()
        {
            validate();
            return new CronCalendar( this );
        }
    }
}
