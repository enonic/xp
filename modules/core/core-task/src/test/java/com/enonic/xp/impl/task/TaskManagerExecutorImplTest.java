package com.enonic.xp.impl.task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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
