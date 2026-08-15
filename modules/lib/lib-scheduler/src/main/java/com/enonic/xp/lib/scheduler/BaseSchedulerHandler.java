package com.enonic.xp.lib.scheduler;

import java.time.Duration;
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

    protected ScheduleCalendar buildCalendar( final Map<String, ?> value )
    {
        return Optional.ofNullable( value ).
            map( calendarScriptValue -> {
                final ScheduleCalendarType type = ScheduleCalendarType.valueOf( string( calendarScriptValue, "type" ) );
                switch ( type )
                {
                    case CRON:
                        return calendarService.get().cron( string( calendarScriptValue, "value" ),
                                                           TimeZone.getTimeZone( string( calendarScriptValue, "timeZone" ) ) );
                    case ONE_TIME:
                        return calendarService.get()
                            .oneTime( Instant.parse( string( calendarScriptValue, "value" ) ),
                                      Boolean.parseBoolean( string( calendarScriptValue, "deleteAfterRun" ) ) );
                    case FIXED_RATE:
                        return calendarService.get().fixedRate( Duration.parse( string( calendarScriptValue, "value" ) ) );
                    default:
                        throw new IllegalArgumentException( String.format( "invalid calendar type: %s", type ) );
                }
            } ).orElseThrow( () -> new NullPointerException( "calendar must be set" ) );
    }

    private static String string( final Map<String, ?> value, final String key )
    {
        // a schedule comes from a script, so its members are not necessarily strings
        return Optional.ofNullable( value.get( key ) ).map( String::valueOf ).orElse( null );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.schedulerService = context.getService( SchedulerService.class );
        this.calendarService = context.getService( CalendarService.class );
    }
}
