package com.enonic.xp.portal.impl.main;

import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.condition.Condition;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;

@Component
public final class AppBootstrapBarrierImpl
    implements AppBootstrapBarrier
{
    static final String BOOTSTRAP_CONDITION_ID = "com.enonic.xp.portal.app.bootstrapped";

    static final String APPLICATION_PROPERTY = "application";

    private static final long WAIT_SECONDS = 300;

    private static final Logger LOG = LoggerFactory.getLogger( AppBootstrapBarrierImpl.class );

    private final BundleContext context;

    @Activate
    public AppBootstrapBarrierImpl( final BundleContext context )
    {
        this.context = context;
    }

    @Override
    public void await( final ApplicationKey applicationKey )
    {
        final String filter =
            "(&(" + Condition.CONDITION_ID + "=" + BOOTSTRAP_CONDITION_ID + ")(" + APPLICATION_PROPERTY + "=" + applicationKey + "))";
        final Filter parsedFilter;
        try
        {
            if ( !context.getServiceReferences( Condition.class, filter ).isEmpty() )
            {
                // fast path: the application has already bootstrapped
                return;
            }
            parsedFilter = context.createFilter( filter );
        }
        catch ( InvalidSyntaxException e )
        {
            throw new IllegalStateException( "Invalid bootstrap condition filter: " + filter, e );
        }
        waitForCondition( parsedFilter, applicationKey );
    }

    private void waitForCondition( final Filter filter, final ApplicationKey applicationKey )
    {
        final ServiceTracker<Condition, Condition> tracker = new ServiceTracker<>( context, filter, null );
        tracker.open();
        try
        {
            if ( tracker.waitForService( TimeUnit.SECONDS.toMillis( WAIT_SECONDS ) ) == null )
            {
                // fail open: a hanging main.js must not dam the application forever
                LOG.warn( "Application {} has not bootstrapped within {}s - proceeding without waiting", applicationKey, WAIT_SECONDS );
            }
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException( "Interrupted while waiting for " + applicationKey + " to bootstrap", e );
        }
        finally
        {
            tracker.close();
        }
    }
}
