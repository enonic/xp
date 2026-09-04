package com.enonic.xp.impl.server.rest.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuditLogApiHandlerTest
{
    private TaskService taskService;

    private AuditLogApiHandler handler;

    @BeforeEach
    void setUp()
    {
        taskService = mock( TaskService.class );
        handler = new AuditLogApiHandler( taskService );
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
        when( taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
    }

    @Test
    void prune()
    {
        assertEquals( HttpStatus.ACCEPTED, handler.handle( request( HttpMethod.POST, "/server:auditlog/prune", "{\"ageThreshold\":\"P30D\"}" ) ).getStatus() );
        assertEquals( HttpStatus.ACCEPTED, handler.handle( request( HttpMethod.POST, "/server:auditlog/cleanup", "{\"ageThreshold\":\"P30D\"}" ) ).getStatus() );
    }

    @Test
    void pruneRequiresThreshold()
    {
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:auditlog/prune", "{}" ) ).getStatus() );
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:auditlog/prune" ) ).getStatus() );
    }
}
