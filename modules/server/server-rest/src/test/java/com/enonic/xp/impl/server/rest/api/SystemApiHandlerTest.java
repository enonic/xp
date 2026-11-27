package com.enonic.xp.impl.server.rest.api;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;


import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.withVirtualHostContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SystemApiHandlerTest
{
    private TaskService taskService;

    private SystemApiHandler handler;

    @BeforeEach
    void setUp()
    {
        taskService = mock( TaskService.class );
        handler = new SystemApiHandler( taskService );
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
        when( taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
        handler.addReporter( new TextReporter( "dump.threads" ) );
        handler.addReporter( new TextReporter( "http.serverdump" ) );
    }

    @Test
    void prune()
    {
        assertEquals( HttpStatus.ACCEPTED, handler.handle( request( HttpMethod.POST, "/server:system/prune" ) ).getStatus() );
        assertEquals( HttpStatus.ACCEPTED,
                      handler.handle( request( HttpMethod.POST, "/server:system/prune", "{\"ageThreshold\":\"PT48H\",\"tasks\":[\"BinaryBlobVacuumTask\"]}" ) ).getStatus() );
    }

    @Test
    void vacuumIsAnAlias()
    {
        assertEquals( HttpStatus.ACCEPTED, handler.handle( request( HttpMethod.POST, "/server:system/vacuum" ) ).getStatus() );

        final Map<String, String> locked = Map.of( "api.server:system.verbs", "-" );
        assertEquals( HttpStatus.FORBIDDEN, withVirtualHostContext( locked, () -> handler.handle( request( HttpMethod.POST, "/server:system/vacuum" ) ) ).getStatus() );

        final Map<String, String> pruneOnly = Map.of( "api.server:system.verbs", "prune" );
        assertEquals( HttpStatus.ACCEPTED, withVirtualHostContext( pruneOnly, () -> handler.handle( request( HttpMethod.POST, "/server:system/vacuum" ) ) ).getStatus() );
    }

    @Test
    void listReports()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:system/reports" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "[\"dump.threads\",\"http.serverdump\"]", response.getBody() );
    }

    @Test
    void report()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:system/reports/dump.threads" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( MediaType.PLAIN_TEXT_UTF_8, response.getContentType() );
        assertEquals( "dump.threads", new String( (byte[]) response.getBody(), StandardCharsets.UTF_8 ) );
    }

    @Test
    void reportNotFound()
    {
        assertEquals( HttpStatus.NOT_FOUND, handler.handle( request( HttpMethod.GET, "/server:system/reports/jvm.info" ) ).getStatus() );
    }

    @Test
    void removedReportNotFound()
    {
        final TextReporter reporter = new TextReporter( "dump.deadlocks" );
        handler.addReporter( reporter );
        handler.removeReporter( reporter );

        assertEquals( HttpStatus.NOT_FOUND, handler.handle( request( HttpMethod.GET, "/server:system/reports/dump.deadlocks" ) ).getStatus() );
    }

    @Test
    void reportVerb()
    {
        final Map<String, String> pruneOnly = Map.of( "api.server:system.verbs", "prune" );
        assertEquals( HttpStatus.FORBIDDEN,
                      withVirtualHostContext( pruneOnly, () -> handler.handle( request( HttpMethod.GET, "/server:system/reports/dump.threads" ) ) )
                          .getStatus() );

        final Map<String, String> reportOnly = Map.of( "api.server:system.verbs", "report" );
        assertEquals( HttpStatus.OK,
                      withVirtualHostContext( reportOnly, () -> handler.handle( request( HttpMethod.GET, "/server:system/reports/dump.threads" ) ) )
                          .getStatus() );
    }

    private record TextReporter(String name)
        implements StatusReporter
    {
        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public MediaType getMediaType()
        {
            return MediaType.PLAIN_TEXT_UTF_8;
        }

        @Override
        public void report( final OutputStream stream )
            throws IOException
        {
            stream.write( name.getBytes( StandardCharsets.UTF_8 ) );
        }
    }
}
