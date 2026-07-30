package com.enonic.xp.impl.task;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.concurrent.SimpleExecutor;

@Component(service = TaskManagerExecutor.class)
public class TaskManagerExecutorImpl
    implements TaskManagerExecutor, ServiceTrackerCustomizer<Application, Application>
{
    private static final Logger LOG = LoggerFactory.getLogger( TaskManagerExecutorImpl.class );

    private final BundleContext context;

    private final ServiceTracker<Application, Application> tracker;

    private final ConcurrentMap<ApplicationKey, ApplicationTaskExecutor> applicationExecutors = new ConcurrentHashMap<>();

    private final SimpleExecutor sharedExecutor;

    private record ApplicationTaskExecutor(ServiceReference<Application> reference, SimpleExecutor executor) {}

    @Activate
    public TaskManagerExecutorImpl( final BundleContext context )
    {
        this.context = context;
        this.sharedExecutor = SimpleExecutor.ofVirtual( "task-manager-thread", e -> LOG.error( "Task execution failed", e ) );
        this.tracker = new ServiceTracker<>( context, Application.class, this );
        this.tracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        tracker.close();
        sharedExecutor.shutdownAndAwaitTermination( Duration.ofSeconds( 5 ),
                                                    neverCommenced -> LOG.warn( "Not all tasks were executed" ) );
    }

    @Override
    public void execute( final ApplicationKey applicationKey, final Runnable command )
    {
        final ApplicationTaskExecutor applicationExecutor = applicationExecutors.get( applicationKey );
        ( applicationExecutor != null ? applicationExecutor.executor() : sharedExecutor ).execute( command );
    }

    @Override
    public Application addingService( final ServiceReference<Application> reference )
    {
        final Application application = context.getService( reference );
        if ( application == null )
        {
            return null;
        }
        final ApplicationKey applicationKey = application.getKey();
        final ApplicationTaskExecutor created = new ApplicationTaskExecutor( reference, SimpleExecutor.ofVirtual(
            "task-manager-" + applicationKey + "-thread-", e -> LOG.error( "Task execution failed", e ) ) );
        final ApplicationTaskExecutor stale = applicationExecutors.put( applicationKey, created );
        if ( stale != null )
        {
            shutdown( applicationKey, stale );
        }
        return application;
    }

    @Override
    public void modifiedService( final ServiceReference<Application> reference, final Application application )
    {
    }

    @Override
    public void removedService( final ServiceReference<Application> reference, final Application application )
    {
        // the application stopped or is being redeployed: its tasks belong to the gone incarnation - stop them,
        // so the executor never outlives the incarnation it was created for. The shut-down executor stays in the
        // map as a tombstone: a submit racing or following the stop is rejected instead of silently landing on
        // the shared executor, where the application's lifecycle could never reach it. A successor incarnation
        // replaces the tombstone in addingService.
        final ApplicationKey applicationKey = application.getKey();
        final ApplicationTaskExecutor current = applicationExecutors.get( applicationKey );
        if ( current != null && current.reference() == reference )
        {
            shutdown( applicationKey, current );
        }
        context.ungetService( reference );
    }

    private static void shutdown( final ApplicationKey applicationKey, final ApplicationTaskExecutor applicationExecutor )
    {
        LOG.debug( "Stopping task executor for {}", applicationKey );
        final boolean terminated =
            applicationExecutor.executor().shutdownAndAwaitTermination( Duration.ZERO, neverCommenced -> failNotCommenced( applicationKey, neverCommenced ) );
        if ( !terminated )
        {
            LOG.warn( "Some tasks of application {} are still running after its task executor shutdown", applicationKey );
        }
    }

    static void failNotCommenced( final ApplicationKey applicationKey, final List<Runnable> neverCommenced )
    {
        for ( final Runnable runnable : neverCommenced )
        {
            if ( runnable instanceof TaskRunnable taskRunnable )
            {
                taskRunnable.failNotCommenced( "Application " + applicationKey + " stopped" );
            }
        }
    }
}
