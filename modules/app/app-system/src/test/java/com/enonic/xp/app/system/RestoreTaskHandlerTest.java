package com.enonic.xp.app.system;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestoreTaskHandlerTest
    extends ScriptTestSupport
{
    @Captor
    private ArgumentCaptor<RestoreParams> paramsCaptor;

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
    void restore()
    {
        final TaskId taskId = TaskId.from( "task" );

        when( taskService.getTaskInfo( taskId ) ).thenReturn( TaskInfo.create()
                                                                  .id( taskId )
                                                                  .name( "com.enonic.xp.app.system:restore" )
                                                                  .application( ApplicationKey.SYSTEM )
                                                                  .startTime( Instant.now() )
                                                                  .build() );
        when( snapshotService.restore( any( RestoreParams.class ) ) ).thenReturn(
            RestoreResult.create().name( "my-snapshot" ).message( "Restored" ).build() );

        TaskProgressReporterContext.withContext( ( _, _ ) -> runFunction( "/test/RestoreTaskHandlerTest.js", "restore" ) )
            .run( taskId, progressReporter );

        verify( snapshotService, times( 1 ) ).restore( paramsCaptor.capture() );

        assertEquals( "my-snapshot", paramsCaptor.getValue().getSnapshotName() );
        assertEquals( RepositoryId.from( "my-repo" ), paramsCaptor.getValue().getRepositoryId() );
        assertFalse( paramsCaptor.getValue().isLatest() );
        assertTrue( paramsCaptor.getValue().isForce() );
    }

    @Test
    void restoreLatest()
    {
        final TaskId taskId = TaskId.from( "task" );

        when( taskService.getTaskInfo( taskId ) ).thenReturn( TaskInfo.create()
                                                                  .id( taskId )
                                                                  .name( "com.enonic.xp.app.system:restore" )
                                                                  .application( ApplicationKey.SYSTEM )
                                                                  .startTime( Instant.now() )
                                                                  .build() );
        when( snapshotService.restore( any( RestoreParams.class ) ) ).thenReturn( RestoreResult.create().message( "Restored" ).build() );

        TaskProgressReporterContext.withContext(
            ( id, progressReporter ) -> runFunction( "/test/RestoreTaskHandlerTest.js", "restoreLatest" ) ).run( taskId, progressReporter );

        verify( snapshotService, times( 1 ) ).restore( paramsCaptor.capture() );

        assertNull( paramsCaptor.getValue().getSnapshotName() );
        assertNull( paramsCaptor.getValue().getRepositoryId() );
        assertTrue( paramsCaptor.getValue().isLatest() );
        assertFalse( paramsCaptor.getValue().isForce() );
    }
}
