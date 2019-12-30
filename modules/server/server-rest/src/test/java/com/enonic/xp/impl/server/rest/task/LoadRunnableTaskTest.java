package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.impl.server.rest.model.SystemLoadRequestJson;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.task.AbstractRunnableTaskTest;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoadRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    @TempDir
    public Path temporaryFolder;

    private DumpService dumpService;

    private ExportService exportService;

    private RepositoryService repositoryService;

    private NodeRepositoryService nodeRepositoryService;

    private Path dumpDir;

    private Path dataDir;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.dumpService = Mockito.mock( DumpService.class );
        this.exportService = Mockito.mock( ExportService.class );
        this.repositoryService = Mockito.mock( RepositoryService.class );
        this.nodeRepositoryService = Mockito.mock( NodeRepositoryService.class );

        final Path homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) ).toAbsolutePath();
        System.setProperty( "xp.home", homeDir.toString() );

        this.dataDir = Files.createDirectory( homeDir.resolve( "data" ) );
        this.dumpDir = Files.createDirectory( dataDir.resolve( "dump" ) );
    }

    @Override
    protected LoadRunnableTask createAndRunTask()
    {
        return null;
    }

    protected LoadRunnableTask createAndRunTask( final SystemLoadRequestJson params )
    {
        final LoadRunnableTask task = LoadRunnableTask.create().
            description( "dump" ).
            taskService( taskService ).
            dumpService( dumpService ).
            exportService( exportService ).
            repositoryService( repositoryService ).
            nodeRepositoryService( nodeRepositoryService ).
            params( params ).
            build();

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    @Test
    public void load()
        throws Exception
    {
        Path nameDir = Files.createDirectory( dumpDir.resolve( "name" ) );

        final NodeImportResult importResult = NodeImportResult.create().
            added( NodePath.create( "/path/to/node1" ).build() ).
            updated( NodePath.create( "/path/to/node2" ).build() ).
            dryRun( true ).
            build();

        Mockito.when( this.exportService.importNodes( Mockito.isA( ImportNodesParams.class ) ) ).thenReturn( importResult );

        Mockito.when( this.repositoryService.list() ).thenReturn( Repositories.from( Repository.create().
            branches( Branch.from( "master" ) ).
            id( RepositoryId.from( "my-repo" ) ).
            build() ) );

        Files.writeString( nameDir.resolve( "export.properties" ), "a=b" );

        SystemLoadParams params = SystemLoadParams.create().dumpName( "name" ).includeVersions( true ).build();

        SystemLoadResult systemLoadResult = SystemLoadResult.create().add( RepoLoadResult.create( RepositoryId.from( "my-repo" ) ).add(
            BranchLoadResult.create( Branch.create().value( "branch-value" ).build() ).error(
                LoadError.error( "error-message" ) ).successful( 2L ).build() ).versions(
            VersionsLoadResult.create().error( LoadError.error( "version-load-error-message" ) ).successful(
                1L ).build() ).build() ).build();

        Mockito.when( this.dumpService.load( Mockito.isA( SystemLoadParams.class ) ) ).thenReturn( systemLoadResult );

        final LoadRunnableTask task = createAndRunTask( new SystemLoadRequestJson( params.getDumpName(), params.isUpgrade() ) );

        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "dump" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "load_result.json" ), jsonTestHelper.stringToJson( result ) );
    }

    @Test
    public void load_system()
        throws Exception
    {
        Files.createDirectory( dumpDir.resolve( "name" ) );

        SystemLoadParams params = SystemLoadParams.create().dumpName( "name" ).includeVersions( true ).build();

        SystemLoadResult systemLoadResult = SystemLoadResult.create().add( RepoLoadResult.create( RepositoryId.from( "my-repo" ) ).add(
            BranchLoadResult.create( Branch.create().value( "branch-value" ).build() ).error(
                LoadError.error( "error-message" ) ).successful( 2L ).build() ).versions(
            VersionsLoadResult.create().error( LoadError.error( "version-load-error-message" ) ).successful(
                1L ).build() ).build() ).build();

        Mockito.when( this.dumpService.load( Mockito.isA( SystemLoadParams.class ) ) ).thenReturn( systemLoadResult );

        final LoadRunnableTask task = createAndRunTask( new SystemLoadRequestJson( params.getDumpName(), params.isUpgrade() ) );

        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "dump" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "load_system_result.json" ), jsonTestHelper.stringToJson( result ) );

    }

    @Test
    public void load_no_dump()
        throws Exception
    {
        final IllegalArgumentException ex =
            assertThrows( IllegalArgumentException.class, () -> createAndRunTask( new SystemLoadRequestJson( "name", false ) ) );
        assertEquals( "No dump with name 'name' found in " + dataDir, ex.getMessage() );
    }
}
