package com.enonic.wem.jaxrs.internal;

import javax.ws.rs.ext.Provider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.enonic.wem.jaxrs.JaxRsContributor;
import com.enonic.wem.jaxrs.JaxRsResourceFactory;

public final class JaxRsTracker
    extends ServiceTracker<JaxRsContributor, JaxRsContributor>
{
    private final JaxRsContainer container;

    public JaxRsTracker( final BundleContext context, final JaxRsContainer container )
    {
        super( context, JaxRsContributor.class.getName(), null );
        this.container = container;
    }

    @Override
    public JaxRsContributor addingService( final ServiceReference<JaxRsContributor> reference )
    {
        final JaxRsContributor service = super.addingService( reference );
        registerAll( service );
        return service;
    }

    @Override
    public void removedService( final ServiceReference<JaxRsContributor> reference, final JaxRsContributor service )
    {
        restartContainer();
        super.removedService( reference, service );
    }

    private void registerAll( final JaxRsContributor instance )
    {
        instance.getObjects().forEach( this::registerInstance );
    }

    public void restartContainer()
    {
        this.container.restart();
        getTracked().values().forEach( this::registerAll );
    }

    private void registerInstance( final Object instance )
    {
        if ( isResourceFactory( instance ) )
        {
            this.container.registerFactory( (JaxRsResourceFactory) instance );
        }
        else if ( isJaxRsProvider( instance ) )
        {
            this.container.registerProvider( instance );
        }
        else
        {
            this.container.registerResource( instance );
        }
    }

    private boolean isResourceFactory( final Object instance )
    {
        return instance instanceof JaxRsResourceFactory;
    }

    private boolean isJaxRsProvider( final Object instance )
    {
        return instance.getClass().getAnnotation( Provider.class ) != null;
    }
}
