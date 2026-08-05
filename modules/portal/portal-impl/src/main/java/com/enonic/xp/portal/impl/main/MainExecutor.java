package com.enonic.xp.portal.impl.main;

import java.util.concurrent.CompletableFuture;

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
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

/**
 * Executes each application's {@code main.js} once it becomes active.
 * <p>
 * Applications are OSGi {@link Application} services, registered while active and unregistered on
 * stop, so this component simply tracks them: DS activation is gated on the deploy-ready Condition
 * (so nothing runs until the initial deployment is complete), and opening the tracker replays every
 * already-active application plus delivers future ones — no {@code ApplicationListener}, no missed
 * events regardless of boot order.
 */
// a pure side-effect component: it executes main.js and provides no service
@Component(immediate = true, service = {})
public final class MainExecutor
    implements ServiceTrackerCustomizer<Application, Application>
{
    private static final Logger LOG = LoggerFactory.getLogger( MainExecutor.class );

    /**
     * Gates activation: {@code main.js} does not run until the initial application deployment is
     * complete, so the tracker's open-time replay sees the full set. Injected only to gate; never
     * dereferenced.
     */
    @SuppressWarnings("unused")
    @Reference(target = "(" + Condition.CONDITION_ID + "=com.enonic.xp.server.deploy.ready)")
    private volatile Condition deployReady;

    private final PortalScriptService scriptService;

    private final BundleContext bundleContext;

    private final ServiceTracker<Application, Application> tracker;

    @Activate
    public MainExecutor( @Reference final PortalScriptService scriptService, final BundleContext bundleContext )
    {
        this.scriptService = scriptService;
        this.bundleContext = bundleContext;
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
        if ( application == null )
        {
            // unregistered between the tracker event and getService: nothing to track or execute
            return null;
        }
        executeMain( ResourceKey.from( application.getKey(), "/main.js" ) );
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

    private void executeMain( final ResourceKey key )
    {
        if ( this.scriptService.hasScript( key ) )
        {
            final CompletableFuture<ScriptExports> completableFuture = this.scriptService.executeAsync( key );
            completableFuture.whenComplete( ( u, e ) -> {
                if ( e != null )
                {
                    LOG.error( "Error while executing {} Application controller", key.getApplicationKey(), e );
                }
                else
                {
                    LOG.debug( "Completed execution of {} Application controller", key.getApplicationKey() );
                }
            } );
        }
    }
}
