package com.enonic.xp.portal.impl.main;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.condition.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationListener;
import com.enonic.xp.core.internal.Dictionaries;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

/**
 * Executes each application's {@code main.js} on activation and publishes a per-application
 * bootstrap {@link Condition} once it completes — the signal controllers await before running
 * (<a href="https://github.com/enonic/xp/issues/7821">#7821</a>). The Condition is a plain
 * string-identified marker in the OSGi service registry: bootstrap state lives there, not in a
 * bespoke service.
 */
@Component(immediate = true)
public final class MainExecutor
    implements ApplicationListener
{
    private static final Logger LOG = LoggerFactory.getLogger( MainExecutor.class );

    private final PortalScriptService scriptService;

    private final BundleContext bundleContext;

    private final ConcurrentMap<ApplicationKey, ServiceRegistration<Condition>> conditions = new ConcurrentHashMap<>();

    @Activate
    public MainExecutor( @Reference final PortalScriptService scriptService, final BundleContext bundleContext )
    {
        this.scriptService = scriptService;
        this.bundleContext = bundleContext;
    }

    @Override
    public void activated( final Application app )
    {
        final ApplicationKey applicationKey = app.getKey();
        final ResourceKey mainScript = ResourceKey.from( applicationKey, "/main.js" );

        if ( this.scriptService.hasScript( mainScript ) )
        {
            this.scriptService.executeAsync( mainScript ).whenComplete( ( exports, e ) -> {
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

    @Override
    public void deactivated( final Application app )
    {
        unregister( this.conditions.remove( app.getKey() ) );
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
