package com.enonic.xp.core.impl.app.event;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.core.impl.app.ApplicationHelper;
import com.enonic.xp.event.EventPublisher;

@Component(immediate = true)
public final class ApplicationEventDispatcher
    implements SynchronousBundleListener
{
    private EventPublisher eventPublisher;

    @Activate
    public void start( final BundleContext context )
    {
        context.addBundleListener( this );
        for ( final Bundle bundle : context.getBundles() )
        {
            if ( !isNonSystemApplication( bundle ) )
            {
                continue;
            }

            if ( bundle.getState() == Bundle.ACTIVE )
            {
                publishApplicationChangeEvent( new BundleEvent( BundleEvent.STARTED, bundle ) );
            }
        }
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        final Bundle bundle = event.getBundle();

        // we cannot check if the bundle is an application when it is uninstalled
        if ( event.getType() == BundleEvent.UNINSTALLED )
        {
            publishApplicationChangeEvent( event );
            return;
        }

        if ( !isNonSystemApplication( bundle ) )
        {
            return;
        }

        publishApplicationChangeEvent( event );
    }

    private boolean isNonSystemApplication( final Bundle bundle )
    {
        return ( bundle.getState() != Bundle.UNINSTALLED ) && ApplicationHelper.isApplication( bundle ) &&
            !ApplicationHelper.isSystemApplication( bundle );
    }

    private void publishApplicationChangeEvent( final BundleEvent event )
    {
        this.eventPublisher.publish( ApplicationEvents.event( event ) );
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }
}
