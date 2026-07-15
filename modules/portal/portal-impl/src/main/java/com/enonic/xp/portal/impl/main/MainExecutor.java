package com.enonic.xp.portal.impl.main;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.condition.Condition;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

/**
 * Triggers each application's {@code main.js} bootstrap once it becomes active
 * (<a href="https://github.com/enonic/xp/issues/7821">#7821</a>). The script engine runs
 * {@code main.js} before serving any controller and gates concurrent executions on it; this
 * component only makes the bootstrap eager, so a side-effect-only application (listeners, tasks) is
 * initialized at deploy rather than waiting for a first request.
 * <p>
 * Applications are OSGi {@link Application} services, registered while active and unregistered on
 * stop, so this component simply tracks them: DS activation is gated on the deploy-ready Condition
 * (so nothing runs until the initial deployment is complete), and opening the tracker replays every
 * already-active application plus delivers future ones — no {@code ApplicationListener}, no missed
 * events regardless of boot order.
 */
@Component(immediate = true)
public final class MainExecutor
    implements ServiceTrackerCustomizer<Application, Application>
{
    private static final Logger LOG = LoggerFactory.getLogger( MainExecutor.class );

    /**
     * Gates activation: {@code main.js} does not run until the initial application deployment is
     * complete, so the tracker's open-time replay sees the full set. Injected only to gate; never
     * dereferenced.
     */
    @Reference(target = "(" + Condition.CONDITION_ID + "=com.enonic.xp.server.deploy.ready)")
    private volatile Condition deployReady;

    private final PortalScriptService scriptService;

    private final BundleContext bundleContext;

    private final Executor bootstrapExecutor;

    private final ServiceTracker<Application, Application> tracker;

    @Activate
    public MainExecutor( @Reference final PortalScriptService scriptService, final BundleContext bundleContext )
    {
        // a virtual thread per bootstrap: main.js may block or run long, and it must not hold up the
        // ServiceTracker callback thread (bundle activation) - no pool to size or shut down
        this( scriptService, bundleContext, Thread.ofVirtual().name( "main-bootstrap-", 0 )::start );
    }

    MainExecutor( final PortalScriptService scriptService, final BundleContext bundleContext, final Executor bootstrapExecutor )
    {
        this.scriptService = scriptService;
        this.bundleContext = bundleContext;
        this.bootstrapExecutor = bootstrapExecutor;
        this.tracker = new ServiceTracker<>( bundleContext, Application.class, this );
        this.tracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        this.tracker.close();
    }

    @Override
    public Application addingService( final ServiceReference<Application> reference )
    {
        final Application application = this.bundleContext.getService( reference );
        bootstrap( application.getKey() );
        return application;
    }

    @Override
    public void modifiedService( final ServiceReference<Application> reference, final Application application )
    {
    }

    @Override
    public void removedService( final ServiceReference<Application> reference, final Application application )
    {
        this.bundleContext.ungetService( reference );
    }

    private void bootstrap( final ApplicationKey applicationKey )
    {
        final ResourceKey mainScript = ResourceKey.from( applicationKey, "/main.js" );
        CompletableFuture.runAsync( () -> this.scriptService.bootstrap( mainScript ), this.bootstrapExecutor )
            .whenComplete( ( result, e ) -> {
                if ( e != null )
                {
                    LOG.error( "Error while bootstrapping {}", applicationKey, e );
                }
                else
                {
                    LOG.debug( "Completed bootstrap of {}", applicationKey );
                }
            } );
    }
}
