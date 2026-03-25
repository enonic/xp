package com.enonic.xp.app.system;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnapshotTaskHandlerTest
    extends ScriptTestSupport
{
    @Captor
    private ArgumentCaptor<SnapshotParams> paramsCaptor;

    @Mock
    private SnapshotService snapshotService;

    @Mock
    private TaskService taskService;

    @Mock
    private ProgressReporter progressReporter;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        addService( SnapshotService.class, this.snapshotService );
        addService( TaskService.class, this.taskService );
    }

    @Test
    void snapshot()
    {
        final TaskId taskId = TaskId.from( "task" );

        when( taskService.getTaskInfo( taskId ) ).thenReturn( TaskInfo.create()
                                                                  .id( taskId )
                                                                  .name( "com.enonic.xp.app.system:snapshot" )
                                                                  .application( ApplicationKey.SYSTEM )
                                                                  .startTime( Instant.now() )
                                                                  .build() );
        when( snapshotService.snapshot( any( SnapshotParams.class ) ) ).thenReturn(
            SnapshotResult.create().name( "my-snapshot" ).state( SnapshotResult.State.SUCCESS ).build() );

        TaskProgressReporterContext.withContext( ( _, _ ) -> runFunction( "/test/SnapshotTaskHandlerTest.js", "snapshot" ) )
            .run( taskId, progressReporter );

        verify( snapshotService, times( 1 ) ).snapshot( paramsCaptor.capture() );

        assertEquals( "my-snapshot", paramsCaptor.getValue().getSnapshotName() );
        assertEquals( RepositoryId.from( "my-repo" ), paramsCaptor.getValue().getRepositoryId() );
    }

    @Test
    void snapshotDefaultParams()
    {
        final TaskId taskId = TaskId.from( "task" );

        when( taskService.getTaskInfo( taskId ) ).thenReturn( TaskInfo.create()
                                                                  .id( taskId )
                                                                  .name( "com.enonic.xp.app.system:snapshot" )
                                                                  .application( ApplicationKey.SYSTEM )
                                                                  .startTime( Instant.now() )
                                                                  .build() );
        when( snapshotService.snapshot( any( SnapshotParams.class ) ) ).thenReturn(
            SnapshotResult.create().name( "auto-generated" ).state( SnapshotResult.State.SUCCESS ).build() );

        TaskProgressReporterContext.withContext( ( _, _ ) -> runFunction( "/test/SnapshotTaskHandlerTest.js", "snapshotDefault" ) )
            .run( taskId, progressReporter );

        verify( snapshotService, times( 1 ) ).snapshot( paramsCaptor.capture() );

        assertNotNull( paramsCaptor.getValue().getSnapshotName() );
        assertNull( paramsCaptor.getValue().getRepositoryId() );
    }
}
