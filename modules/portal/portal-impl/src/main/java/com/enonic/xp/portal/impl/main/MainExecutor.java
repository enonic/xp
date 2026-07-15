package com.enonic.xp.portal.impl.main;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
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
import com.enonic.xp.core.internal.Dictionaries;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

/**
 * Runs each application's {@code main.js} and publishes a per-application bootstrap
 * {@link Condition} once it completes — the signal controllers await before running
 * (<a href="https://github.com/enonic/xp/issues/7821">#7821</a>).
 * <p>
 * Applications are OSGi {@link Application} services, registered while active and unregistered on
 * stop, so this component simply tracks them: DS activation is gated on the deploy-ready Condition
 * (so nothing runs until the initial deployment is complete), and opening the tracker replays
 * every already-active application plus delivers future ones — no {@code ApplicationListener}, no
 * late-listener catch-up, no missed events regardless of boot order (<a
 * href="https://github.com/enonic/xp/issues/12200">#12200</a>).
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

    private final ConcurrentMap<ApplicationKey, ServiceRegistration<Condition>> conditions = new ConcurrentHashMap<>();

    @Activate
    public MainExecutor( @Reference final PortalScriptService scriptService, final BundleContext bundleContext )
    {
        this( scriptService, bundleContext, Executors.newVirtualThreadPerTaskExecutor() );
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
        if ( this.bootstrapExecutor instanceof ExecutorService executorService )
        {
            executorService.shutdown();
        }
    }

    @Override
    public Application addingService( final ServiceReference<Application> reference )
    {
        final Application application = this.bundleContext.getService( reference );
        // an Application service exists only while the application is active, so its appearance is
        // the activation signal
        bootstrap( application.getKey() );
        return application;
    }

    @Override
    public void modifiedService( final ServiceReference<Application> reference, final Application application )
    {
        // properties only; nothing to do
    }

    @Override
    public void removedService( final ServiceReference<Application> reference, final Application application )
    {
        unregister( this.conditions.remove( application.getKey() ) );
        this.bundleContext.ungetService( reference );
    }

    private void bootstrap( final ApplicationKey applicationKey )
    {
        final ResourceKey mainScript = ResourceKey.from( applicationKey, "/main.js" );
        if ( this.scriptService.hasScript( mainScript ) )
        {
            // run within a bootstrap scope so re-entrant executions main.js triggers (e.g. a task
            // it submits) don't wait for the bootstrap Condition that only publishes once it returns
            CompletableFuture.runAsync( () -> BootstrapScope.run( () -> this.scriptService.execute( mainScript ) ), this.bootstrapExecutor )
                .whenComplete( ( result, e ) -> {
                    if ( e != null )
                    {
                        LOG.error( "Error while executing {} Application controller", applicationKey, e );
                    }
                    else
                    {
                        LOG.debug( "Completed execution of {} Application controller", applicationKey );
                    }
                    // publish on success AND failure: a broken main.js surfaces in the log, not as a
                    // permanently un-bootstrapped application
                    publishBootstrapped( applicationKey );
                } );
        }
        else
        {
            // no main.js: the application is trivially bootstrapped
            publishBootstrapped( applicationKey );
        }
    }

    private void publishBootstrapped( final ApplicationKey applicationKey )
    {
        final ServiceRegistration<Condition> registration = this.bundleContext.registerService( Condition.class, Condition.INSTANCE,
                                                                                                Dictionaries.copyOf(
                                                                                                    Map.of( Condition.CONDITION_ID,
                                                                                                            AppBootstrapBarrierImpl.BOOTSTRAP_CONDITION_ID,
                                                                                                            AppBootstrapBarrierImpl.APPLICATION_PROPERTY,
                                                                                                            applicationKey.toString() ) ) );
        // replace any registration left by a previous incarnation of the same application
        unregister( this.conditions.put( applicationKey, registration ) );
    }

    private static void unregister( final ServiceRegistration<Condition> registration )
    {
        if ( registration != null )
        {
            try
            {
                registration.unregister();
            }
            catch ( IllegalStateException e )
            {
                // already unregistered (bundle stopping) - nothing to do
            }
        }
    }
}
