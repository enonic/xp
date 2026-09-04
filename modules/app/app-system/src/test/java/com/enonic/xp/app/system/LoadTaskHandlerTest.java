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
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoadTaskHandlerTest
    extends ScriptTestSupport
{
    private final JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @Captor
    private ArgumentCaptor<SystemLoadParams> paramsCaptor;

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
    void load()
    {
        final SystemLoadResult systemLoadResult = SystemLoadResult.create()
            .add( RepoLoadResult.create( RepositoryId.from( "my-repo" ) )
                      .add( BranchLoadResult.create( Branch.create().value( "branch-value" ).build() )
                                .error( LoadError.error( "error-message" ) )
                                .successful( 2L )
                                .build() )
                      .versions( VersionsLoadResult.create().error( LoadError.error( "version-load-error-message" ) ).successful( 1L ).build() )
                      .build() )
            .build();
        when( dumpService.load( any( SystemLoadParams.class ) ) ).thenReturn( systemLoadResult );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/LoadTaskHandlerTest.js", "load" ) )
            .run( task(), progressReporter );

        verify( dumpService ).load( paramsCaptor.capture() );
        assertEquals( "name", paramsCaptor.getValue().getDumpName() );
        assertFalse( paramsCaptor.getValue().isUpgrade() );
        assertTrue( paramsCaptor.getValue().isIncludeVersions() );

        final ArgumentCaptor<ProgressReportParams> progressCaptor = ArgumentCaptor.forClass( ProgressReportParams.class );
        verify( progressReporter ).progress( progressCaptor.capture() );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "load_system_result.json" ),
                                         jsonTestHelper.stringToJson( progressCaptor.getValue().getMessage() ) );
    }

    @Test
    void loadRepositories()
    {
        when( dumpService.load( any( SystemLoadParams.class ) ) ).thenReturn( SystemLoadResult.create().build() );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/LoadTaskHandlerTest.js", "loadRepositories" ) )
            .run( task(), progressReporter );

        verify( dumpService ).load( paramsCaptor.capture() );
        assertTrue( paramsCaptor.getValue().isUpgrade() );
        assertEquals( RepositoryIds.from( RepositoryId.from( "my-repo" ) ), paramsCaptor.getValue().getRepositories() );
    }

    private TaskId task()
    {
        final TaskId taskId = TaskId.from( "task" );
        when( taskService.getTaskInfo( taskId ) ).thenReturn( TaskInfo.create()
                                                                  .id( taskId )
                                                                  .name( "com.enonic.xp.app.system:load" )
                                                                  .application( ApplicationKey.SYSTEM )
                                                                  .startTime( Instant.now() )
                                                                  .build() );
        return taskId;
    }
}
