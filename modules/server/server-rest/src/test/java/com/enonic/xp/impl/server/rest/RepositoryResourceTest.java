package com.enonic.xp.impl.server.rest;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.impl.server.rest.model.RepositoriesJson;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RepositoryResourceTest
    extends JaxRsResourceTestSupport
{
    private TaskService taskService;

    private RepositoryService repoService;

    private RepositoryResource resource;

    @TempDir
    public Path temporaryFolder;

    @BeforeEach
    void setup()
        throws Exception
    {
        final Path homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) ).toAbsolutePath();
        System.setProperty( "xp.home", homeDir.toString() );
    }

    @Override
    protected RepositoryResource getResourceInstance()
    {
        taskService = mock( TaskService.class );
        repoService = mock( RepositoryService.class );

        resource = new RepositoryResource();
        resource.setTaskService( taskService );
        resource.setRepositoryService( repoService );

        return resource;
    }

    @Test
    void exportNodes()
        throws Exception
    {
        when( taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "task-id" ) );

        final String result = request().path( "repo/export" )
            .entity( readFromFile( "export_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        final ArgumentCaptor<SubmitLocalTaskParams> captor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );

        verify( taskService, times( 1 ) ).submitLocalTask( captor.capture() );
        assertThat( captor.getValue() ).extracting( SubmitLocalTaskParams::getName,
                                                                         SubmitLocalTaskParams::getDescription )
            .containsExactly( null, "Export my_export" );

        assertStringJson( "{\"taskId\" : \"task-id\"}", result );
    }

    @Test
    void importNodes()
        throws Exception
    {
        when( taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "task-id" ) );

        final String result = request().path( "repo/import" )
            .entity( readFromFile( "import_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        final ArgumentCaptor<SubmitLocalTaskParams> captor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );

        verify( taskService, times( 1 ) ).submitLocalTask( captor.capture() );
        assertThat( captor.getValue() ).extracting( SubmitLocalTaskParams::getName,
                                                    SubmitLocalTaskParams::getDescription )
            .containsExactly( null, "Import my_export" );

        assertStringJson( "{\"taskId\" : \"task-id\"}", result );
    }

    @Test
    void listRepositories()
    {
        Repositories.Builder builder = Repositories.create();
        for ( int i = 0; i < 5; i++ )
        {
            builder.add( Repository.create().id( RepositoryId.from( "repo-" + i ) ).branches( RepositoryConstants.MASTER_BRANCH ).build() );
        }
        when( repoService.list() ).thenReturn( builder.build() );

        final RepositoriesJson result = resource.listRepositories();
        assertEquals( 5, result.repositories.size() );
    }
}
