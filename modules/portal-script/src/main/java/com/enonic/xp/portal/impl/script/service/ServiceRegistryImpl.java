package com.enonic.xp.portal.impl.script.service;

import org.osgi.framework.BundleContext;

public final class ServiceRegistryImpl
    implements ServiceRegistry
{
    private final BundleContext bundleContext;

    public ServiceRegistryImpl( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    @Override
    public <T> ServiceRef<T> getService( final Class<T> type )
    {
        return new ServiceRefImpl<>( type, this.bundleContext );
    }
}
