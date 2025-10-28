package com.enonic.xp.impl.task;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.core.internal.concurrent.RecurringJob;
import com.enonic.xp.core.internal.osgi.OsgiSupportMock;
import com.enonic.xp.event.Event;
import com.enonic.xp.impl.task.distributed.TaskContext;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocalTaskManagerImplTest
{
    private static final TaskContext TEST_TASK_CONTEXT = TaskContext.create()
        .setBranch( Branch.from( "master" ) )
        .setRepo( RepositoryId.from( "test" ) )
        .setAuthInfo( AuthenticationInfo.unAuthenticated() )
        .build();

    private LocalTaskManagerImpl taskMan;

    private List<Event> eventsPublished;

    private TaskManagerCleanupSchedulerMock cleanupScheduler;

    @BeforeEach
    void setup()
    {
        LocalTaskManagerImpl.clock = Clock.fixed( Instant.ofEpochSecond( 0 ), ZoneOffset.UTC );

        cleanupScheduler = new TaskManagerCleanupSchedulerMock();

        taskMan = new LocalTaskManagerImpl( Runnable::run, cleanupScheduler, event -> this.eventsPublished.add( event ) );
        taskMan.activate();

        this.eventsPublished = new ArrayList<>();

        final Bundle bundle = OsgiSupportMock.mockBundle();
        when( bundle.getSymbolicName() ).thenReturn( "some.app" );
    }

    @AfterEach
    void tearDown()
    {
        taskMan.deactivate();
        cleanupScheduler.verifyStopped();
    }

    @Test
    void submitTask()
    {
        final RunnableTask runnableTask = ( id, progressReporter ) -> {
            for ( int i = 0; i < 5; i++ )
            {
                progressReporter.progress( 1, 10 );
                progressReporter.info( "Step " + i );
            }
        };

        assertEquals( 0, taskMan.getAllTasks().size() );
        assertEquals( 0, taskMan.getRunningTasks().size() );
        assertNull( taskMan.getTaskInfo( TaskId.from( "1" ) ) );

        final DescribedTaskImpl describedTask = new DescribedTaskImpl( runnableTask, "task-1", "task 1", TEST_TASK_CONTEXT );
        taskMan.submitTask( describedTask );

        assertNotNull( taskMan.getTaskInfo( describedTask.getTaskId() ) );

        for ( int i = 0; i < 25; i++ )
        {
            final TaskInfo taskInfo = taskMan.getTaskInfo( describedTask.getTaskId() );
            System.out.printf( "Task %s, details: %s\r\n", describedTask.getTaskId(), taskInfo );
            System.out.flush();
        }

        assertEquals( 1, taskMan.getAllTasks().size() );
        assertEquals( 0, taskMan.getRunningTasks().size() );
        assertNotNull( taskMan.getTaskInfo( describedTask.getTaskId() ) );
        assertEquals( 13, eventsPublished.size() );
        assertEquals( "task.submitted , task.updated , task.updated , task.updated , task.updated , task.updated , " +
                          "task.updated , task.updated , task.updated , task.updated , task.updated , task.updated , task.finished",
                      eventTypes() );
    }

    @Test
    void submitTaskWithError()
    {
        final RunnableTask runnableTask = ( id, progressReporter ) -> {
            throw new RuntimeException( "Some error" );
        };

        assertEquals( 0, taskMan.getAllTasks().size() );
        assertEquals( 0, taskMan.getRunningTasks().size() );
        assertNull( taskMan.getTaskInfo( TaskId.from( "1" ) ) );

        final DescribedTaskImpl describedTask = new DescribedTaskImpl( runnableTask, "task-1", "task 1", TEST_TASK_CONTEXT );
        taskMan.submitTask( describedTask );

        assertNotNull( taskMan.getTaskInfo( describedTask.getTaskId() ) );

        assertEquals( 1, taskMan.getAllTasks().size() );
        assertEquals( 0, taskMan.getRunningTasks().size() );
        assertEquals( TaskState.FAILED, taskMan.getTaskInfo( describedTask.getTaskId() ).getState() );
        assertEquals( 4, eventsPublished.size() );
        assertEquals( "task.submitted , task.updated , task.updated , task.failed", eventTypes() );
    }

    @Test
    void testRemoveExpiredTasks()
        throws InterruptedException
    {
        LocalTaskManagerImpl.clock = Clock.fixed( Instant.ofEpochSecond( 0 ), ZoneId.systemDefault() );

        CountDownLatch latch = new CountDownLatch( 1 );
        RunnableTask runnableTask = ( id, progressReporter ) -> {
            latch.countDown();
        };

        final DescribedTaskImpl describedTask = new DescribedTaskImpl( runnableTask, "task-1", "task 1", TEST_TASK_CONTEXT );
        taskMan.submitTask( describedTask );

        cleanupScheduler.rerun();

        assertNotNull( taskMan.getTaskInfo( describedTask.getTaskId() ) );
        assertEquals( 1, taskMan.getAllTasks().size() );
        assertTrue( taskMan.getRunningTasks().size() <= 1 );

        latch.await();

        LocalTaskManagerImpl.clock = Clock.offset( LocalTaskManagerImpl.clock, Duration.ofSeconds( LocalTaskManagerImpl.KEEP_COMPLETED_MAX_TIME_SEC + 1 ) );
        cleanupScheduler.rerun();

        assertNull( taskMan.getTaskInfo( describedTask.getTaskId() ) );
        assertEquals( 0, taskMan.getAllTasks().size() );
        assertEquals( 0, taskMan.getRunningTasks().size() );
        assertEquals( 4, eventsPublished.size() );
        assertEquals( "task.submitted , task.updated , task.finished , task.removed", eventTypes() );
    }

    private String eventTypes()
    {
        return eventsPublished.stream().map( ( e ) -> e.getType() ).collect( Collectors.joining( " , " ) );
    }

    private static class TaskManagerCleanupSchedulerMock
        implements TaskManagerCleanupScheduler
    {
        private Runnable command;

        private RecurringJob scheduledFutureMock;

        @Override
        public RecurringJob scheduleWithFixedDelay( final Runnable command )
        {
            this.command = command;
            command.run();
            scheduledFutureMock = mock( RecurringJob.class );
            return scheduledFutureMock;
        }

        public void rerun()
        {
            command.run();
        }

        public void verifyStopped()
        {
            verify( scheduledFutureMock ).cancel();
        }
    }
}
