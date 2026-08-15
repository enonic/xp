package com.enonic.xp.impl.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.impl.scheduler.distributed.CronCalendarImpl;
import com.enonic.xp.impl.scheduler.distributed.FixedDelayCalendarImpl;
import com.enonic.xp.impl.scheduler.distributed.OneTimeCalendarImpl;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.FixedDelayCalendar;
import com.enonic.xp.scheduler.OneTimeCalendar;

@Component(immediate = true)
public class CalendarServiceImpl
    implements CalendarService
{

    @Override
    public CronCalendar cron( final String value, final TimeZone timeZone )
    {
        if ( CronCalendarImpl.isCronValue( value ) )
        {
            return CronCalendarImpl.create().
                value( value ).
                timeZone( timeZone ).
                build();
        }
        throw new IllegalArgumentException( String.format( "'value' param is not a cron value: '%s'", value ) );
    }

    @Override
    public OneTimeCalendar oneTime( final Instant value )
    {
        return oneTime( value, false );
    }

    @Override
    public OneTimeCalendar oneTime( final Instant value, final boolean deleteAfterRun )
    {
        return OneTimeCalendarImpl.create().
            value( value ).
            deleteAfterRun( deleteAfterRun ).
            build();
    }

    @Override
    public FixedDelayCalendar fixedDelay( final Duration duration )
    {
        return FixedDelayCalendarImpl.create().
            duration( duration ).
            build();
    }
}
