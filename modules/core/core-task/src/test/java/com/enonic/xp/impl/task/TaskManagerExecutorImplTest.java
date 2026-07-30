package com.enonic.xp.impl.task;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.impl.task.distributed.DescribedTask;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskManagerExecutorImplTest
{
    @Test
    void lifecycle()
        throws Exception
    {
        final BundleContext bundleContext = mock( BundleContext.class );

        Phaser phaser = new Phaser( 2 );
        final TaskManagerExecutorImpl taskManagerExecutor = new TaskManagerExecutorImpl( bundleContext );
        taskManagerExecutor.execute( ApplicationKey.from( "myapp" ), phaser::arriveAndAwaitAdvance );

        phaser.arriveAndAwaitAdvance();
        taskManagerExecutor.deactivate();
    }

    @Test
    void addingService_noService()
    {
        final BundleContext bundleContext = mock( BundleContext.class );
        final ServiceReference<Application> reference = mock( ServiceReference.class );

        final TaskManagerExecutorImpl taskManagerExecutor = new TaskManagerExecutorImpl( bundleContext );
        try
        {
            assertNull( taskManagerExecutor.addingService( reference ) );
            taskManagerExecutor.modifiedService( reference, null );
        }
        finally
        {
            taskManagerExecutor.deactivate();
        }
    }

    @Test
    void addingService_staleExecutorReplacedAndStopped()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final BundleContext bundleContext = mock( BundleContext.class );
        final ServiceReference<Application> reference = mock( ServiceReference.class );
        final ServiceReference<Application> newReference = mock( ServiceReference.class );
        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( applicationKey );
        when( bundleContext.getService( reference ) ).thenReturn( application );
        when( bundleContext.getService( newReference ) ).thenReturn( application );

        final TaskManagerExecutorImpl taskManagerExecutor = new TaskManagerExecutorImpl( bundleContext );
        try
        {
            taskManagerExecutor.addingService( reference );

            final CountDownLatch started = new CountDownLatch( 1 );
            final CountDownLatch interrupted = new CountDownLatch( 1 );
            taskManagerExecutor.execute( applicationKey, () -> {
                started.countDown();
                try
                {
                    Thread.sleep( 60_000 );
                }
                catch ( InterruptedException e )
                {
                    interrupted.countDown();
                }
            } );

            assertTrue( started.await( 5, TimeUnit.SECONDS ) );

            // a new incarnation registering replaces the stale executor and stops it
            taskManagerExecutor.addingService( newReference );

            assertTrue( interrupted.await( 5, TimeUnit.SECONDS ) );

            // removal of the old incarnation must not stop the new incarnation's executor
            taskManagerExecutor.removedService( reference, application );

            final CountDownLatch executed = new CountDownLatch( 1 );
            taskManagerExecutor.execute( applicationKey, executed::countDown );
            assertTrue( executed.await( 5, TimeUnit.SECONDS ) );
        }
        finally
        {
            taskManagerExecutor.deactivate();
        }
    }

    @Test
    void neverCommencedTasksAreFailed()
    {
        final InternalProgressReporter progressReporter = mock( InternalProgressReporter.class );
        final DescribedTask task = mock( DescribedTask.class );
        final TaskRunnable taskRunnable = new TaskRunnable( task, progressReporter );

        TaskManagerExecutorImpl.failNotCommenced( ApplicationKey.from( "myapp" ), List.of( taskRunnable, () -> {
        } ) );

        verify( progressReporter ).failed( "Application myapp stopped" );
    }

    @Test
    void applicationTasksInterruptedOnApplicationRemoval()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final BundleContext bundleContext = mock( BundleContext.class );
        final ServiceReference<Application> reference = mock( ServiceReference.class );
        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( applicationKey );
        when( bundleContext.getService( reference ) ).thenReturn( application );

        final TaskManagerExecutorImpl taskManagerExecutor = new TaskManagerExecutorImpl( bundleContext );
        try
        {
            taskManagerExecutor.addingService( reference );

            final CountDownLatch started = new CountDownLatch( 1 );
            final CountDownLatch interrupted = new CountDownLatch( 1 );
            taskManagerExecutor.execute( applicationKey, () -> {
                started.countDown();
                try
                {
                    Thread.sleep( 60_000 );
                }
                catch ( InterruptedException e )
                {
                    interrupted.countDown();
                }
            } );

            assertTrue( started.await( 5, TimeUnit.SECONDS ) );

            taskManagerExecutor.removedService( reference, application );

            assertTrue( interrupted.await( 5, TimeUnit.SECONDS ) );
        }
        finally
        {
            taskManagerExecutor.deactivate();
        }
    }
}
