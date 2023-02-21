package com.enonic.xp.impl.server.rest.task;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpError;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.impl.server.rest.model.SystemDumpRequestJson;
import com.enonic.xp.impl.server.rest.task.listener.SystemDumpListenerImpl;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DumpRunnableTaskTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    DumpService dumpService;

    TaskService taskService;

    @BeforeEach
    void setUp()
    {
        this.dumpService = mock( DumpService.class );
        this.taskService = mock( TaskService.class );
    }

    private DumpRunnableTask createTask( final SystemDumpRequestJson params )
    {
        return DumpRunnableTask.create()
            .taskService( taskService )
            .dumpService( dumpService )
            .name( params.getName() )
            .includeVersions( params.isIncludeVersions() )
            .archive( params.isArchive() )
            .maxAge( params.getMaxAge() )
            .maxVersions( params.getMaxVersions() )
            .build();
    }

    @Test
    void dump()
    {
        final SystemDumpResult systemDumpResult = SystemDumpResult.create()
            .add( RepoDumpResult.create( RepositoryId.from( "my-repo" ) )
                      .versions( 3L )
                      .add( BranchDumpResult.create( Branch.create().value( "branch-value" ).build() )
                                .addedNodes( 3 )
                                .error( DumpError.error( "error-message" ) )
                                .build() )
                      .build() )
            .build();

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        final SystemDumpParams params = SystemDumpParams.create()
            .dumpName( "dump" )
            .includeBinaries( true )
            .includeVersions( true )
            .maxAge( 10 )
            .maxVersions( 20 )
            .listener( new SystemDumpListenerImpl( progressReporter ) )
            .build();

        final TaskId taskId = TaskId.from( "taskId" );
        when( taskService.getTaskInfo( taskId ) ).thenReturn(
            TaskInfo.create().id( taskId ).name( "dump" ).application( ApplicationKey.SYSTEM ).startTime( Instant.now() ).build() );

        when( this.dumpService.dump( any( SystemDumpParams.class ) ) ).thenReturn( systemDumpResult );

        final DumpRunnableTask task = createTask(
            new SystemDumpRequestJson( params.getDumpName(), params.isIncludeVersions(), params.getMaxAge(), params.getMaxVersions(),
                                       params.isArchive() ) );


        task.run( TaskId.from( "taskId" ), progressReporter );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, Mockito.times( 1 ) ).info( progressReporterCaptor.capture() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "dump_result.json" ), jsonTestHelper.stringToJson( result ) );
    }
}
