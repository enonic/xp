package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.function.IntConsumer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.repo.impl.RepositoryEvents;

@Component(immediate = true)
public class ClusterRestarter
    implements EventListener
{
    private final BundleContext bundleContext;

    @Activate
    public ClusterRestarter( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event.isType( RepositoryEvents.RESTORE_INITIALIZED_EVENT_TYPE ) )
        {
            setFrameworkPhase( 1 );
        }
        else if ( event.isType( RepositoryEvents.RESTORED_EVENT_TYPE ) )
        {
            setFrameworkPhase( 2 );
        }
    }

    void setFrameworkPhase( final int phase )
    {
        final ServiceReference<?> serviceReference =
            bundleContext.getServiceReference( "com.enonic.xp.launcher.impl.framework.FrameworkLifecycleService" );
        final IntConsumer service = (IntConsumer) bundleContext.getService( serviceReference );
        try
        {
            service.accept( phase );
        }
        finally
        {
            bundleContext.ungetService( serviceReference );
        }
    }
}
