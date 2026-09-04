package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

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
}
