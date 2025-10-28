package com.enonic.xp.impl.server.rest;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.impl.server.rest.model.CleanUpAuditLogRequestJson;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;

class AuditLogResourceTest
    extends JaxRsResourceTestSupport
{
    @TempDir
    public Path temporaryFolder;

    private TaskService taskService;

    private AuditLogResource resource;

    @BeforeEach
    void setup()
        throws Exception
    {
        final Path homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) ).toAbsolutePath();
        System.setProperty( "xp.home", homeDir.toString() );
    }

    @Test
    void cleanUpAuditLog()
    {
        Mockito.when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "task-id" ) );

        final CleanUpAuditLogRequestJson requestJson = new CleanUpAuditLogRequestJson( "PT1s" );

        final TaskResultJson result = resource.cleanup( requestJson );

        final ArgumentCaptor<SubmitTaskParams> captor = ArgumentCaptor.forClass( SubmitTaskParams.class );

        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( captor.capture() );

        assertEquals( "task-id", result.getTaskId() );

        assertEquals( 1, captor.getValue().getData().getTotalSize() );
        assertEquals( "PT1s", captor.getValue().getData().getString( "ageThreshold" ) );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.taskService = mock( TaskService.class );

        resource = new AuditLogResource();
        resource.setTaskService( taskService );
        return resource;
    }
}
