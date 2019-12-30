package com.enonic.xp.impl.server.rest;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.enonic.xp.impl.server.rest.model.ExportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.ImportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.RepositoriesJson;
import com.enonic.xp.impl.server.rest.task.ExportRunnableTask;
import com.enonic.xp.impl.server.rest.task.ImportRunnableTask;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskResultJson;
import com.enonic.xp.task.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

public class RepositoryResourceTest
    extends ServerRestTestSupport
{
    private TaskService taskService;

    private RepositoryService repoService;

    private RepositoryResource resource;

    @TempDir
    public Path temporaryFolder;

    @BeforeEach
    public void setup()
        throws Exception
    {
        final Path homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) ).toAbsolutePath();
        System.setProperty( "xp.home", homeDir.toString() );
    }

    @Override
    protected RepositoryResource getResourceInstance()
    {
        taskService = Mockito.mock( TaskService.class );
        repoService = Mockito.mock( RepositoryService.class );

        resource = new RepositoryResource();
        resource.setTaskService( taskService );
        resource.setRepositoryService( repoService );

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

    @Test
    public void listRepositories()
    {
        Repositories.Builder builder = Repositories.create();
        for ( int i = 0; i < 5; i++ )
        {
            builder.add( Repository.create().id( RepositoryId.from( "repo-" + i ) ).branches( RepositoryConstants.MASTER_BRANCH ).build() );
        }
        Mockito.when( repoService.list() ).thenReturn( builder.build() );

        final RepositoriesJson result = resource.listRepositories();
        assertEquals( 5, result.repositories.size() );
    }
}
