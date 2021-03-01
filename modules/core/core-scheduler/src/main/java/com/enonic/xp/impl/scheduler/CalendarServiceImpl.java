package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.impl.scheduler.distributed.CronCalendar;
import com.enonic.xp.impl.scheduler.distributed.OneTimeCalendar;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.ScheduleCalendar;

@Component(immediate = true)
public class CalendarServiceImpl
    implements CalendarService
{

    @Override
    public ScheduleCalendar cron( final String value, final TimeZone timeZone )
    {
        if ( CronCalendar.isCronValue( value ) )
        {
            return CronCalendar.create().
                value( value ).
                timeZone( timeZone ).
                build();
        }
        throw new IllegalArgumentException( String.format( "'value' param is not a cron value: '%s'", value ) );
    }

    @Override
    public ScheduleCalendar oneTime( final Instant value )
    {
        return OneTimeCalendar.create().
            value( value ).
            build();
    }
}
