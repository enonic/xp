package com.enonic.xp.impl.server.rest.api;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.impl.server.rest.model.ScheduledJobJson;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:scheduler} - scheduled jobs: list, get, create and delete.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:scheduler", "title=Scheduler API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class SchedulerApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:scheduler";

    private final SchedulerService schedulerService;

    private final CalendarService calendarService;

    @Activate
    public SchedulerApiHandler( @Reference final SchedulerService schedulerService, @Reference final CalendarService calendarService )
    {
        super( KEY );
        this.schedulerService = schedulerService;
        this.calendarService = calendarService;

        route( HttpMethod.GET, "/", "list", this::list );
        route( HttpMethod.POST, "/", "create", this::create );
        route( HttpMethod.GET, "/{name}", "get", this::get );
        route( HttpMethod.DELETE, "/{name}", "delete", this::delete );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        return json( Map.of( "jobs", schedulerService.list().stream().map( ScheduledJobJson::new ).toList() ) );
    }

    private WebResponse get( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final ScheduledJob job = schedulerService.get( ScheduledJobName.from( params.get( "name" ) ) );
        if ( job == null )
        {
            return notFound( params );
        }
        return json( new ScheduledJobJson( job ) );
    }

    private WebResponse create( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final CreateJson create = body( request, CreateJson.class );
        if ( create.name() == null || create.name().isBlank() )
        {
            throw new IllegalArgumentException( "[name] is required" );
        }
        if ( create.descriptor() == null || create.descriptor().isBlank() )
        {
            throw new IllegalArgumentException( "[descriptor] is required" );
        }
        if ( create.schedule() == null )
        {
            throw new IllegalArgumentException( "[schedule] is required" );
        }

        final ScheduledJob job = schedulerService.create( CreateScheduledJobParams.create()
                                                              .name( ScheduledJobName.from( create.name() ) )
                                                              .descriptor( DescriptorKey.from( create.descriptor() ) )
                                                              .calendar( calendar( create.schedule() ) )
                                                              .description( create.description() )
                                                              .config( PropertyTree.fromMap( create.config() == null ? Map.of() : create.config() ) )
                                                              .enabled( create.enabled() )
                                                              .user( Optional.ofNullable( create.user() ).map( PrincipalKey::from ).orElse( null ) )
                                                              .build() );
        return json( HttpStatus.CREATED, new ScheduledJobJson( job ) );
    }

    private WebResponse delete( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        if ( !schedulerService.delete( ScheduledJobName.from( params.get( "name" ) ) ) )
        {
            return notFound( params );
        }
        return json( Map.of( "name", params.get( "name" ) ) );
    }

    private WebResponse notFound( final Map<String, String> params )
    {
        return error( HttpStatus.NOT_FOUND, String.format( "Scheduled job [%s] not found", params.get( "name" ) ) );
    }

    private ScheduleCalendar calendar( final ScheduleJson schedule )
    {
        if ( schedule.type() == null )
        {
            throw new IllegalArgumentException( "[schedule.type] is required: CRON, ONE_TIME or FIXED_RATE" );
        }
        if ( schedule.value() == null )
        {
            throw new IllegalArgumentException( "[schedule.value] is required" );
        }
        return switch ( schedule.type() )
        {
            case CRON -> calendarService.cron( schedule.value(), TimeZone.getTimeZone(
                Optional.ofNullable( schedule.timeZone() ).orElseThrow( () -> new IllegalArgumentException( "[schedule.timeZone] is required for CRON" ) ) ) );
            case ONE_TIME -> calendarService.oneTime( Instant.parse( schedule.value() ), schedule.deleteAfterRun() );
            case FIXED_RATE -> calendarService.fixedRate( Duration.parse( schedule.value() ) );
        };
    }

    public record CreateJson(String name, String descriptor, String description, ScheduleJson schedule, Map<String, Object> config,
                             Boolean enabled, String user)
    {
        public CreateJson
        {
            enabled = enabled == null || enabled;
        }
    }

    public record ScheduleJson(ScheduleCalendarType type, String value, String timeZone, boolean deleteAfterRun)
    {
    }
}
