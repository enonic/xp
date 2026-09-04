package com.enonic.xp.impl.server.rest.api;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.FixedRateCalendar;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.withVirtualHostContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SchedulerApiHandlerTest
{
    private SchedulerService schedulerService;

    private CalendarService calendarService;

    private SchedulerApiHandler handler;

    @BeforeEach
    void setUp()
    {
        schedulerService = mock( SchedulerService.class );
        calendarService = mock( CalendarService.class );
        handler = new SchedulerApiHandler( schedulerService, calendarService );
    }

    @Test
    void list()
    {
        when( schedulerService.list() ).thenReturn( List.of( job( "nightly" ) ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:scheduler" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( String.valueOf( response.getBody() ).startsWith( "{\"jobs\":[{" ) );
        assertTrue( String.valueOf( response.getBody() ).contains( "\"name\":\"nightly\"" ) );
    }

    @Test
    void get()
    {
        when( schedulerService.get( ScheduledJobName.from( "nightly" ) ) ).thenReturn( job( "nightly" ) );

        assertEquals( HttpStatus.OK, handler.handle( request( HttpMethod.GET, "/server:scheduler/nightly" ) ).getStatus() );
        assertEquals( HttpStatus.NOT_FOUND, handler.handle( request( HttpMethod.GET, "/server:scheduler/other" ) ).getStatus() );
    }

    @Test
    void createCron()
    {
        final CronCalendar cron = mock( CronCalendar.class );
        when( calendarService.cron( eq( "0 3 * * *" ), any( TimeZone.class ) ) ).thenReturn( cron );
        when( schedulerService.create( any() ) ).thenReturn( job( "nightly" ) );

        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:scheduler", """
            {"name":"nightly","descriptor":"com.enonic.xp.app.system:dump","description":"Nightly dump",
             "schedule":{"type":"CRON","value":"0 3 * * *","timeZone":"Europe/Oslo"},
             "config":{"name":"nightly"},"user":"user:system:su"}
            """ ) );

        assertEquals( HttpStatus.CREATED, response.getStatus() );

        final ArgumentCaptor<CreateScheduledJobParams> captor = ArgumentCaptor.forClass( CreateScheduledJobParams.class );
        verify( schedulerService ).create( captor.capture() );
        final CreateScheduledJobParams params = captor.getValue();
        assertEquals( "nightly", params.getName().getValue() );
        assertEquals( "com.enonic.xp.app.system:dump", params.getDescriptor().toString() );
        assertEquals( cron, params.getCalendar() );
        assertEquals( "nightly", params.getConfig().getString( "name" ) );
        assertEquals( PrincipalKey.from( "user:system:su" ), params.getUser() );
        assertTrue( params.isEnabled() );
        verify( calendarService ).cron( "0 3 * * *", TimeZone.getTimeZone( "Europe/Oslo" ) );
    }

    @Test
    void createOneTimeAndFixedRate()
    {
        when( calendarService.oneTime( any( Instant.class ), eq( true ) ) ).thenReturn( mock( OneTimeCalendar.class ) );
        when( calendarService.fixedRate( Duration.parse( "PT1H" ) ) ).thenReturn( mock( FixedRateCalendar.class ) );
        when( schedulerService.create( any() ) ).thenReturn( job( "once" ) );

        assertEquals( HttpStatus.CREATED, handler.handle( request( HttpMethod.POST, "/server:scheduler",
                                                                   "{\"name\":\"once\",\"descriptor\":\"a:b\",\"schedule\":{\"type\":\"ONE_TIME\",\"value\":\"2030-01-01T00:00:00Z\",\"deleteAfterRun\":true}}" ) ).getStatus() );
        assertEquals( HttpStatus.CREATED, handler.handle( request( HttpMethod.POST, "/server:scheduler",
                                                                   "{\"name\":\"hourly\",\"descriptor\":\"a:b\",\"enabled\":false,\"schedule\":{\"type\":\"FIXED_RATE\",\"value\":\"PT1H\"}}" ) ).getStatus() );

        verify( calendarService ).oneTime( Instant.parse( "2030-01-01T00:00:00Z" ), true );
        verify( calendarService ).fixedRate( Duration.parse( "PT1H" ) );
    }

    @Test
    void createValidation()
    {
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:scheduler", "{\"descriptor\":\"a:b\"}" ) ).getStatus() );
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:scheduler", "{\"name\":\"x\",\"descriptor\":\"a:b\"}" ) ).getStatus() );
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:scheduler",
                                                                       "{\"name\":\"x\",\"descriptor\":\"a:b\",\"schedule\":{\"type\":\"CRON\",\"value\":\"* * * * *\"}}" ) ).getStatus() );
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:scheduler",
                                                                       "{\"name\":\"x\",\"descriptor\":\"a:b\",\"schedule\":{\"type\":\"WEEKLY\",\"value\":\"x\"}}" ) ).getStatus() );
        verify( schedulerService, never() ).create( any() );
    }

    @Test
    void delete()
    {
        when( schedulerService.delete( ScheduledJobName.from( "nightly" ) ) ).thenReturn( true );

        assertEquals( HttpStatus.OK, handler.handle( request( HttpMethod.DELETE, "/server:scheduler/nightly" ) ).getStatus() );
        assertEquals( HttpStatus.NOT_FOUND, handler.handle( request( HttpMethod.DELETE, "/server:scheduler/other" ) ).getStatus() );
    }

    @Test
    void readOnlyVhost()
    {
        final Map<String, String> policy = Map.of( "api.server:scheduler.verbs", "list, get" );
        when( schedulerService.list() ).thenReturn( List.of() );

        assertEquals( HttpStatus.OK, withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.GET, "/server:scheduler" ) ) ).getStatus() );
        assertEquals( HttpStatus.FORBIDDEN, withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.DELETE, "/server:scheduler/nightly" ) ) ).getStatus() );
        assertEquals( HttpStatus.FORBIDDEN, withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.POST, "/server:scheduler", "{}" ) ) ).getStatus() );
        verify( schedulerService, never() ).delete( any() );
    }

    private static ScheduledJob job( final String name )
    {
        return ScheduledJob.create()
            .name( ScheduledJobName.from( name ) )
            .descriptor( DescriptorKey.from( "com.enonic.xp.app.system:dump" ) )
            .calendar( mock( ScheduleCalendar.class ) )
            .config( new PropertyTree() )
            .enabled( true )
            .build();
    }
}
