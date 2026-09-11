package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.withVirtualHostContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SchedulerApiHandlerTest
{
    private SchedulerService schedulerService;

    private SchedulerApiHandler handler;

    @BeforeEach
    void setUp()
    {
        schedulerService = mock( SchedulerService.class );
        handler = new SchedulerApiHandler( schedulerService );
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
    void mutationsNotExposed()
    {
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, handler.handle( request( HttpMethod.POST, "/server:scheduler", "{}" ) ).getStatus() );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, handler.handle( request( HttpMethod.DELETE, "/server:scheduler/nightly" ) ).getStatus() );
    }

    @Test
    void readOnlyVhost()
    {
        final Map<String, String> policy = Map.of( "api.server:scheduler.verbs", "list" );
        when( schedulerService.list() ).thenReturn( List.of() );

        assertEquals( HttpStatus.OK, withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.GET, "/server:scheduler" ) ) ).getStatus() );
        assertEquals( HttpStatus.FORBIDDEN, withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.GET, "/server:scheduler/nightly" ) ) ).getStatus() );
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
