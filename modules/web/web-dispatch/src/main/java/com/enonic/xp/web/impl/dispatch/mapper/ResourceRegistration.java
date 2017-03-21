package com.enonic.xp.web.impl.dispatch.mapper;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.web.dispatch.ResourceMapping;

final class ResourceRegistration
{
    private final ResourceMapping mapping;

    private ServiceRegistration<ResourceMapping> registration;

    ResourceRegistration( final ResourceMapping mapping )
    {
        this.mapping = mapping;
        this.registration = null;
    }

    void register( final BundleContext context )
    {
        if ( this.registration != null )
        {
            return;
        }

        this.registration = context.registerService( ResourceMapping.class, this.mapping, null );
    }

    void unregister()
    {
        if ( this.registration == null )
        {
            return;
        }

        try
        {
            this.registration.unregister();
        }
        finally
        {
            this.registration = null;
        }
    }
}
