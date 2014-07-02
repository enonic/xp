package com.enonic.wem.jaxrs.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.enonic.wem.jaxrs.JaxRsContributor;

public final class JaxRsTracker
    extends ServiceTracker<JaxRsContributor, JaxRsContributor>
{
    private final JaxRsListener listener;

    public JaxRsTracker( final BundleContext context, final JaxRsListener listener )
    {
        super( context, JaxRsContributor.class.getName(), null );
        this.listener = listener;
    }

    @Override
    public JaxRsContributor addingService( final ServiceReference<JaxRsContributor> reference )
    {
        final JaxRsContributor service = super.addingService( reference );
        this.listener.add( service );
        return service;
    }

    @Override
    public void removedService( final ServiceReference<JaxRsContributor> reference, final JaxRsContributor service )
    {
        this.listener.remove( service );
        super.removedService( reference, service );
    }
}
