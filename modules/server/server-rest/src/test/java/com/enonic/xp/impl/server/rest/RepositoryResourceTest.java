package com.enonic.xp.impl.server.rest;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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

import static org.mockito.Matchers.eq;

public class RepositoryResourceTest
    extends ServerRestTestSupport
{
    private TaskService taskService;

    private RepositoryService repoService;

    private RepositoryResource resource;

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
            builder.add( Repository.create().id( RepositoryId.from( "repo-" + i ) ).branchInfos( RepositoryConstants.MASTER_BRANCH_INFO ).build() );
        }
        Mockito.when( repoService.list() ).thenReturn( builder.build() );

        final RepositoriesJson result = resource.listRepositories();
        assertEquals( 5, result.repositories.size() );
    }
}
