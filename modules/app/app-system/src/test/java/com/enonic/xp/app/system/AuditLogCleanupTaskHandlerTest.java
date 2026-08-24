package com.enonic.xp.app.system;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.CleanUpAuditLogListener;
import com.enonic.xp.audit.CleanUpAuditLogParams;
import com.enonic.xp.audit.CleanUpAuditLogResult;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.task.TaskState;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogCleanupTaskHandlerTest
    extends ScriptTestSupport
{
    @Captor
    private ArgumentCaptor<CleanUpAuditLogParams> paramsCaptor;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private TaskService taskService;

    @Mock
    private ProgressReporter progressReporter;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        addService( AuditLogService.class, this.auditLogService );
        addService( TaskService.class, this.taskService );
    }

    @Test
    void cleanUp()
    {
        final TaskId taskId = TaskId.from( "id1" );
        final TaskInfo taskInfo = TaskInfo.create()
            .id( taskId )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( Instant.now() )
            .state( TaskState.RUNNING )
            .build();
        when( taskService.getTaskInfo( taskId ) ).thenReturn( taskInfo );
        when( taskService.getAllTasks() ).thenReturn( List.of(taskInfo) );

        runFunction( "/test/AuditLogCleanupTaskHandlerTest.js", "cleanUp" );
        verify( auditLogService, times( 1 ) ).cleanUp( paramsCaptor.capture() );

        assertEquals( "PT2s", paramsCaptor.getValue().getAgeThreshold() );
    }

    @Test
    void cleanUp_reports_what_it_has_found_and_deleted()
    {
        final TaskId taskId = TaskId.from( "id1" );
        final TaskInfo taskInfo = TaskInfo.create()
            .id( taskId )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( Instant.now() )
            .state( TaskState.RUNNING )
            .build();
        when( taskService.getTaskInfo( taskId ) ).thenReturn( taskInfo );
        when( taskService.getAllTasks() ).thenReturn( List.of( taskInfo ) );
        when( auditLogService.cleanUp( any() ) ).thenAnswer( invocation -> {
            final CleanUpAuditLogListener listener = invocation.getArgument( 0, CleanUpAuditLogParams.class ).getListener();

            listener.resolved( 2000 );
            listener.recordsDeleted( 1000 );
            listener.recordsDeleted( 500 );
            listener.resolved( 2500 );
            listener.recordsDeleted( 999 );

            return CleanUpAuditLogResult.create().build();
        } );

        TaskProgressReporterContext.withContext(
            ( id, reporter ) -> runFunction( "/test/AuditLogCleanupTaskHandlerTest.js", "cleanUp" ) ).run( taskId, progressReporter );

        final ArgumentCaptor<ProgressReportParams> progress = ArgumentCaptor.forClass( ProgressReportParams.class );
        verify( progressReporter, times( 4 ) ).progress( progress.capture() );

        assertThat( progress.getAllValues() ).extracting( ProgressReportParams::getCurrent, ProgressReportParams::getTotal )
            .containsExactly( tuple( 0, 2000 ), tuple( 1000, 2000 ), tuple( 1500, 2500 ), tuple( 2499, 2500 ) );
    }

    @Test
    void other_tasks_loose()
    {
        final Instant now = Instant.now();

        final TaskInfo newer = TaskInfo.create()
            .id( TaskId.from( "id2" ) )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( now.minus( 1, ChronoUnit.DAYS ) )
            .state( TaskState.WAITING )
            .build();

        final TaskInfo finished = TaskInfo.create()
            .id( TaskId.from( "id3" ) )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( now.minus( 2, ChronoUnit.DAYS ) )
            .state( TaskState.FINISHED )
            .build();

        final TaskInfo failed = TaskInfo.create()
            .id( TaskId.from( "id4" ) )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( now.minus( 2, ChronoUnit.DAYS ) )
            .state( TaskState.FAILED )
            .build();

        final TaskInfo different = TaskInfo.create()
            .id( TaskId.from( "id5" ) )
            .name( "com.enonic.xp.app.system:not-audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( now.minus( 2, ChronoUnit.DAYS ) )
            .state( TaskState.RUNNING )
            .build();

        final TaskInfo greaterId = TaskInfo.create()
            .id( TaskId.from( "id6" ) )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( now.minus( 2, ChronoUnit.DAYS ) )
            .state( TaskState.RUNNING )
            .build();

        final TaskInfo taskInfo = TaskInfo.create()
            .id( TaskId.from( "id1" ) )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( now.minus( 2, ChronoUnit.DAYS ) )
            .state( TaskState.RUNNING )
            .build();

        when( taskService.getTaskInfo( TaskId.from( "id1" ) ) ).thenReturn( taskInfo );
        when( taskService.getAllTasks() ).thenReturn( List.of(taskInfo, newer, finished, failed, different, greaterId) );

        runFunction( "/test/AuditLogCleanupTaskHandlerTest.js", "cleanUp" );
        verify( auditLogService, times( 1 ) ).cleanUp( paramsCaptor.capture() );
    }

    @Test
    void older_wins()
    {
        final Instant now = Instant.now();

        final TaskInfo older = TaskInfo.create()
            .id( TaskId.from( "id2" ) )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( now.minus( 1, ChronoUnit.DAYS ) )
            .state( TaskState.WAITING )
            .build();

        final TaskInfo taskInfo = TaskInfo.create()
            .id( TaskId.from( "id1" ) )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( now )
            .state( TaskState.RUNNING )
            .build();

        when( taskService.getTaskInfo( TaskId.from( "id1" ) ) ).thenReturn( taskInfo );
        when( taskService.getAllTasks() ).thenReturn( List.of(taskInfo, older) );

        assertThrows( IllegalStateException.class, () -> runFunction( "/test/AuditLogCleanupTaskHandlerTest.js", "cleanUp" ));
    }

    @Test
    void same_startTime_smaller_id_wins()
    {
        final Instant now = Instant.now();

        final TaskInfo older = TaskInfo.create()
            .id( TaskId.from( "id0" ) )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( now )
            .state( TaskState.WAITING )
            .build();

        final TaskInfo taskInfo = TaskInfo.create()
            .id( TaskId.from( "id1" ) )
            .name( "com.enonic.xp.app.system:audit-log-cleanup" )
            .application( ApplicationKey.SYSTEM )
            .startTime( now )
            .state( TaskState.RUNNING )
            .build();

        when( taskService.getTaskInfo( TaskId.from( "id1" ) ) ).thenReturn( taskInfo );
        when( taskService.getAllTasks() ).thenReturn( List.of(taskInfo, older) );

        assertThrows( IllegalStateException.class, () -> runFunction( "/test/AuditLogCleanupTaskHandlerTest.js", "cleanUp" ));
    }
}
