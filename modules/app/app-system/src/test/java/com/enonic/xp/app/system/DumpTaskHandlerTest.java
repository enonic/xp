package com.enonic.xp.app.system;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpError;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DumpTaskHandlerTest
    extends ScriptTestSupport
{
    private final JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @Captor
    private ArgumentCaptor<SystemDumpParams> paramsCaptor;

    @Mock
    private DumpService dumpService;

    @Mock
    private TaskService taskService;

    @Mock
    private ProgressReporter progressReporter;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        addService( DumpService.class, this.dumpService );
        addService( TaskService.class, this.taskService );
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
        when( dumpService.dump( any( SystemDumpParams.class ) ) ).thenReturn( systemDumpResult );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/DumpTaskHandlerTest.js", "dump" ) )
            .run( task(), progressReporter );

        verify( dumpService ).dump( paramsCaptor.capture() );
        assertEquals( "dump", paramsCaptor.getValue().getDumpName() );
        assertTrue( paramsCaptor.getValue().isIncludeVersions() );
        assertTrue( paramsCaptor.getValue().isIncludeBinaries() );
        assertEquals( 10, paramsCaptor.getValue().getMaxAge() );
        assertEquals( 20, paramsCaptor.getValue().getMaxVersions() );
        assertTrue( paramsCaptor.getValue().getRepositories().isEmpty() );

        final ArgumentCaptor<ProgressReportParams> progressCaptor = ArgumentCaptor.forClass( ProgressReportParams.class );
        verify( progressReporter ).progress( progressCaptor.capture() );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "dump_result.json" ),
                                         jsonTestHelper.stringToJson( progressCaptor.getValue().getMessage() ) );
    }

    @Test
    void dumpRepositories()
    {
        when( dumpService.dump( any( SystemDumpParams.class ) ) ).thenReturn( SystemDumpResult.create().build() );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/DumpTaskHandlerTest.js", "dumpRepositories" ) )
            .run( task(), progressReporter );

        verify( dumpService ).dump( paramsCaptor.capture() );
        assertEquals( RepositoryIds.from( RepositoryId.from( "my-repo" ), RepositoryId.from( "other-repo" ) ),
                      paramsCaptor.getValue().getRepositories() );
    }

    private TaskId task()
    {
        final TaskId taskId = TaskId.from( "task" );
        when( taskService.getTaskInfo( taskId ) ).thenReturn( TaskInfo.create()
                                                                  .id( taskId )
                                                                  .name( "com.enonic.xp.app.system:dump" )
                                                                  .application( ApplicationKey.SYSTEM )
                                                                  .startTime( Instant.now() )
                                                                  .build() );
        return taskId;
    }
}
