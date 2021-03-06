package com.enonic.xp.impl.server.rest;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.enonic.xp.impl.server.rest.model.SystemDumpRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemDumpUpgradeRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemLoadRequestJson;
import com.enonic.xp.impl.server.rest.task.DumpRunnableTask;
import com.enonic.xp.impl.server.rest.task.LoadRunnableTask;
import com.enonic.xp.impl.server.rest.task.UpgradeRunnableTask;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskResultJson;
import com.enonic.xp.task.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;

public class SystemResourceTest
    extends JaxRsResourceTestSupport
{
    @TempDir
    public Path temporaryFolder;

    private TaskService taskService;

    private SystemResource resource;

    @BeforeEach
    public void setup()
        throws Exception
    {
        final Path homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) ).toAbsolutePath();
        System.setProperty( "xp.home", homeDir.toString() );
    }

    @Test
    public void dumpTask()
        throws Exception
    {
        Mockito.when( taskService.submitTask( Mockito.isA( DumpRunnableTask.class ), eq( "dump" ) ) )
            .thenReturn( TaskId.from( "task-id" ) );

        final SystemDumpRequestJson json = Mockito.mock( SystemDumpRequestJson.class );

        final TaskResultJson result = resource.systemDump( json );
        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void load()
        throws Exception
    {
        Mockito.when( taskService.submitTask( Mockito.isA( LoadRunnableTask.class ), eq( "load" ) ) )
            .thenReturn( TaskId.from( "task-id" ) );

        final SystemLoadRequestJson json = Mockito.mock( SystemLoadRequestJson.class );

        final TaskResultJson result = resource.load( json );
        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void vacuum()
        throws Exception
    {
        Mockito.when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "task-id" ) );

        final TaskResultJson result = resource.vacuum( null );
        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void upgrade()
        throws Exception
    {
        Mockito.when( taskService.submitTask( Mockito.isA( UpgradeRunnableTask.class ), eq( "upgrade" ) ) )
            .thenReturn( TaskId.from( "task-id" ) );

        final SystemDumpUpgradeRequestJson json = Mockito.mock( SystemDumpUpgradeRequestJson.class );

        final TaskResultJson result = resource.upgrade( json );
        assertEquals( "task-id", result.getTaskId() );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.taskService = Mockito.mock( TaskService.class );

        resource = new SystemResource();
        resource.setTaskService( taskService );
        return resource;
    }
}
