package com.enonic.xp.script.impl.async;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationBundleUtils;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.concurrent.SimpleExecutor;

@Component(service = ScriptAsyncService.class)
public class ScriptAsyncService
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptAsyncService.class );

    private final BundleTracker<SimpleExecutor> bundleTracker;

    @Activate
    public ScriptAsyncService( final BundleContext context )
    {
        bundleTracker = new BundleTracker<>( context, Bundle.ACTIVE + Bundle.STARTING, new Customizer() );
    }

    @Activate
    public void activate()
    {
        bundleTracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        bundleTracker.close();
    }

    public Executor getAsyncExecutor( ApplicationKey applicationKey )
    {
        return bundleTracker.getTracked().
            entrySet().stream().
            filter( bundleEntry -> applicationKey.equals( ApplicationKey.from( bundleEntry.getKey() ) ) ).
            findAny().
            map( Map.Entry::getValue ).
            orElseThrow( () -> new NoSuchElementException( "No background executor found for app " + applicationKey ) );
    }

    private static class Customizer
        implements BundleTrackerCustomizer<SimpleExecutor>
    {
        @Override
        public SimpleExecutor addingBundle( final Bundle bundle, final BundleEvent event )
        {
            if ( ApplicationBundleUtils.isApplication( bundle ) )
            {
                final ApplicationKey applicationKey = ApplicationKey.from( bundle );
                final long id = bundle.getBundleId();
                return new SimpleExecutor( Executors::newSingleThreadExecutor, "app-" + applicationKey + "-" + id,
                                           e -> LOG.error( "Unhandled error in app background thread {}", applicationKey, e ) );
            }
            else
            {
                return null;
            }
        }

        @Override
        public void modifiedBundle( final Bundle bundle, final BundleEvent event, final SimpleExecutor object )
        {

        }

        @Override
        public void removedBundle( final Bundle bundle, final BundleEvent event, final SimpleExecutor object )
        {
            final ApplicationKey applicationKey = ApplicationKey.from( bundle );
            object.shutdownAndAwaitTermination( Duration.ZERO,
                                                neverCommenced -> LOG.warn( "Some events were not processed by app background thread {}",
                                                                            applicationKey ) );
        }
    }
}
