package com.enonic.xp.impl.server.rest;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.enonic.xp.impl.server.rest.model.ExportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.ImportNodesRequestJson;
import com.enonic.xp.impl.server.rest.task.ExportRunnableTask;
import com.enonic.xp.impl.server.rest.task.ImportRunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskResultJson;
import com.enonic.xp.task.TaskService;

import static org.mockito.Matchers.eq;

public class ExportResourceTest
    extends ServerRestTestSupport
{
    private TaskService taskService;

    private ExportResource resource;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup()
        throws Exception
    {
        final File homeDir = this.temporaryFolder.newFolder( "home" );
        System.setProperty( "xp.home", homeDir.getAbsolutePath() );
    }

    @Override
    protected ExportResource getResourceInstance()
    {
        taskService = Mockito.mock( TaskService.class );

        resource = new ExportResource();
        resource.setTaskService( taskService );

        return resource;
    }

    @Test
    public void exportNodes()
        throws Exception
    {

        Mockito.when( taskService.submitTask( Mockito.isA( ExportRunnableTask.class ), eq( "export" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        final ExportNodesRequestJson json = Mockito.mock( ExportNodesRequestJson.class );

        final TaskResultJson result = resource.exportNodes( json );
        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    public void importNodes()
        throws Exception
    {
        Mockito.when( taskService.submitTask( Mockito.isA( ImportRunnableTask.class ), eq( "import" ) ) ).thenReturn(
            TaskId.from( "task-id" ) );

        final ImportNodesRequestJson json = Mockito.mock( ImportNodesRequestJson.class );

        final TaskResultJson result = resource.importNodes( json );
        assertEquals( "task-id", result.getTaskId() );
    }
}
