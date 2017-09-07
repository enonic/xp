package com.enonic.xp.impl.task;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.google.common.util.concurrent.Uninterruptibles;

import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskState;

import static org.junit.Assert.*;

public class TaskManagerImplTest
{
    private TaskManagerImpl taskMan;

    @Before
    public void setup()
    {
        taskMan = new TaskManagerImpl();
        final AtomicInteger count = new AtomicInteger( 0 );
        taskMan.setIdGen( () -> ( TaskId.from( Integer.toString( count.incrementAndGet() ) ) ) );
    }

    @Test
    public void submitTask()
        throws Exception
    {
        final RunnableTask runnableTask = ( id, progressReporter ) ->
        {
            for ( int i = 0; i < 5; i++ )
            {
                progressReporter.progress( 1, 10 );
                progressReporter.info( "Step " + i );
                Uninterruptibles.sleepUninterruptibly( 500, TimeUnit.MILLISECONDS );
            }
        };

        assertEquals( 0, taskMan.getAllTasks().size() );
        assertEquals( 0, taskMan.getRunningTasks().size() );
        assertNull( taskMan.getTaskInfo( TaskId.from( "1" ) ) );

        final TaskId taskId = taskMan.submitTask( runnableTask, "task 1", "" );

        assertNotNull( taskMan.getTaskInfo( taskId ) );

        for ( int i = 0; i < 25; i++ )
        {
            final TaskInfo taskInfo = taskMan.getTaskInfo( taskId );
            System.out.printf( "Task %s, details: %s\r\n", taskId.toString(), taskInfo.toString() );
            System.out.flush();
            Uninterruptibles.sleepUninterruptibly( 100, TimeUnit.MILLISECONDS );
        }

        assertEquals( 1, taskMan.getAllTasks().size() );
        assertEquals( 0, taskMan.getRunningTasks().size() );
        assertNotNull( taskMan.getTaskInfo( taskId ) );

    }

    @Test
    public void submitTaskWithError()
        throws Exception
    {
        final RunnableTask runnableTask = ( id, progressReporter ) ->
        {
            Uninterruptibles.sleepUninterruptibly( 500, TimeUnit.MILLISECONDS );
            throw new RuntimeException( "Some error" );
        };

        assertEquals( 0, taskMan.getAllTasks().size() );
        assertEquals( 0, taskMan.getRunningTasks().size() );
        assertNull( taskMan.getTaskInfo( TaskId.from( "1" ) ) );

        final TaskId taskId = taskMan.submitTask( runnableTask, "task 1", "" );

        assertNotNull( taskMan.getTaskInfo( taskId ) );

        Uninterruptibles.sleepUninterruptibly( 600, TimeUnit.MILLISECONDS );

        assertEquals( 1, taskMan.getAllTasks().size() );
        assertEquals( 0, taskMan.getRunningTasks().size() );
        assertEquals( TaskState.FAILED, taskMan.getTaskInfo( taskId ).getState() );

    }

    @Test
    public void testRemoveExpiredTasks()
        throws InterruptedException
    {
        Instant initTime = Instant.now();
        taskMan.setClock( Clock.fixed( initTime, ZoneId.systemDefault() ) );

        CountDownLatch latch = new CountDownLatch( 1 );
        RunnableTask runnableTask = ( id, progressReporter ) ->
        {
            Uninterruptibles.sleepUninterruptibly( 100, TimeUnit.MILLISECONDS );
            latch.countDown();
        };

        TaskId taskId = taskMan.submitTask( runnableTask, "task 1", "" );
        taskMan.removeExpiredTasks();

        assertNotNull( taskMan.getTaskInfo( taskId ) );
        assertEquals( 1, taskMan.getAllTasks().size() );
        assertTrue( taskMan.getRunningTasks().size() <= 1 );

        latch.await();
        Uninterruptibles.sleepUninterruptibly( 100, TimeUnit.MILLISECONDS );

        Instant laterTime = initTime.plus( TaskManagerImpl.KEEP_COMPLETED_MAX_TIME_SEC + 1, ChronoUnit.SECONDS );
        taskMan.setClock( Clock.fixed( laterTime, ZoneId.systemDefault() ) );
        taskMan.removeExpiredTasks();

        assertNull( taskMan.getTaskInfo( taskId ) );
        assertEquals( 0, taskMan.getAllTasks().size() );
        assertEquals( 0, taskMan.getRunningTasks().size() );
    }
}