package com.enonic.xp.lib.scheduler;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Supplier;

import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;


public abstract class BaseSchedulerHandler
    implements ScriptBean
{
    protected Supplier<SchedulerService> schedulerService;

    protected Supplier<CalendarService> calendarService;

    public final Object execute()
    {
        validate();
        return this.doExecute();
    }

    protected abstract Object doExecute();

    protected abstract void validate();

    protected ScheduleCalendar buildCalendar( final Map<String, String> value )
    {
        return Optional.ofNullable( value ).
            map( calendarScriptValue -> {
                final ScheduleCalendarType type = ScheduleCalendarType.valueOf( calendarScriptValue.get( "type" ) );
                switch ( type )
                {
                    case CRON:
                        return calendarService.get().cron( calendarScriptValue.get( "value" ),
                                                           TimeZone.getTimeZone( calendarScriptValue.get( "timeZone" ) ) );
                    case ONE_TIME:
                        return calendarService.get().oneTime( Instant.parse( calendarScriptValue.get( "value" ) ) );
                    default:
                        throw new IllegalArgumentException( String.format( "invalid calendar type: %s", type ) );
                }
            } ).orElseThrow( () -> new NullPointerException( "calendar must be set" ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.schedulerService = context.getService( SchedulerService.class );
        this.calendarService = context.getService( CalendarService.class );
    }
}
