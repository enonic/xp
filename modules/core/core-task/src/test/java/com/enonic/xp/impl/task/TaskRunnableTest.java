package com.enonic.xp.impl.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.impl.task.distributed.DescribedTask;
import com.enonic.xp.impl.task.distributed.TaskContext;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.trace.TestTrace;
import com.enonic.xp.trace.Tracer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskRunnableTest
{
    private DescribedTask describedTask;

    private InternalProgressReporter progressReporter;

    @BeforeEach
    void setUp()
    {
        describedTask = mock( DescribedTask.class );
        progressReporter = mock( InternalProgressReporter.class );

        when( describedTask.getTaskId() ).thenReturn( TaskId.from( "task-id" ) );
        when( describedTask.getApplicationKey() ).thenReturn( ApplicationKey.from( "com.enonic.app.test" ) );
        when( describedTask.getName() ).thenReturn( "my-task" );
    }

    @Test
    void runRecordsTraceAttributesOnSuccess()
    {
        final User user = User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build();
        final TaskContext taskContext =
            TaskContext.create().setAuthInfo( AuthenticationInfo.create().user( user ).build() ).build();
        when( describedTask.getTaskContext() ).thenReturn( taskContext );

        // outside OSGi the @Traced wrapper is inert; a manually bound trace exercises the attribute enrichment code
        final TestTrace trace = TestTrace.of( "task.run" );
        Tracer.trace( trace, () -> new TaskRunnable( describedTask, progressReporter ).run() );

        assertEquals( "task-id", trace.get( "taskId" ) );
        assertEquals( "user:system:test-user", trace.get( "user" ) );
        assertEquals( "com.enonic.app.test", trace.get( "app" ) );
        assertEquals( Boolean.TRUE, trace.get( "success" ) );

        verify( describedTask ).run( progressReporter );
        verify( progressReporter ).running();
        verify( progressReporter ).finished();
    }

    @Test
    void runRecordsAnonymousUserAndFailure()
    {
        // no auth info in the task context: the trace must fall back to the anonymous principal
        when( describedTask.getTaskContext() ).thenReturn( TaskContext.create().build() );
        doThrow( new RuntimeException( "task failed" ) ).when( describedTask ).run( progressReporter );

        final TestTrace trace = TestTrace.of( "task.run" );
        Tracer.trace( trace, () -> new TaskRunnable( describedTask, progressReporter ).run() );

        assertEquals( "task-id", trace.get( "taskId" ) );
        assertEquals( PrincipalKey.ofAnonymous().toString(), trace.get( "user" ) );
        assertEquals( "com.enonic.app.test", trace.get( "app" ) );
        assertEquals( Boolean.FALSE, trace.get( "success" ) );

        verify( progressReporter ).running();
        verify( progressReporter ).failed( "task failed" );
    }
}
