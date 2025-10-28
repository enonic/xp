package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.home.HomeDirSupport;
import com.enonic.xp.impl.server.rest.model.SystemLoadRequestJson;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoadRunnableTaskTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );
    @TempDir
    public Path temporaryFolder;

    private DumpService dumpService;

    TaskService taskService;


    private Path dumpDir;

    @BeforeEach
    void setUp()
        throws Exception
    {
        this.dumpService = mock( DumpService.class );
        this.taskService = mock( TaskService.class );

        HomeDirSupport.set( temporaryFolder );

        this.dumpDir = Files.createDirectories( temporaryFolder.resolve( "data" ).resolve( "dump" ) );
    }


    private LoadRunnableTask createTask( final SystemLoadRequestJson params )
    {
        return LoadRunnableTask.create()
            .taskService( taskService )
            .dumpService( dumpService )
            .name( params.getName() )
            .upgrade( params.isUpgrade() )
            .archive( params.isArchive() )
            .build();
    }

    @Test
    void load_system()
        throws Exception
    {
        Files.createDirectory( dumpDir.resolve( "name" ) );

        SystemLoadParams params = SystemLoadParams.create().dumpName( "name" ).includeVersions( true ).build();

        SystemLoadResult systemLoadResult = SystemLoadResult.create()
            .add( RepoLoadResult.create( RepositoryId.from( "my-repo" ) )
                      .add( BranchLoadResult.create( Branch.create().value( "branch-value" ).build() )
                                .error( LoadError.error( "error-message" ) )
                                .successful( 2L )
                                .build() )
                      .versions(
                          VersionsLoadResult.create().error( LoadError.error( "version-load-error-message" ) ).successful( 1L ).build() )
                      .build() )
            .build();

        final TaskId taskId = TaskId.from( "taskId" );
        when( taskService.getTaskInfo( taskId ) ).thenReturn(
            TaskInfo.create().id( taskId ).name( "load" ).application( ApplicationKey.SYSTEM ).startTime( Instant.now() ).build() );

        when( this.dumpService.load( any( SystemLoadParams.class ) ) ).thenReturn( systemLoadResult );

        final LoadRunnableTask task =
            createTask( new SystemLoadRequestJson( params.getDumpName(), params.isUpgrade(), params.isArchive() ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        task.run( taskId, progressReporter );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, times( 1 ) ).info( progressReporterCaptor.capture() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "load_system_result.json" ), jsonTestHelper.stringToJson( result ) );

    }
}
