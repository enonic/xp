package com.enonic.xp.script.impl.service;

import org.osgi.framework.BundleContext;

import static java.util.Objects.requireNonNull;

public final class ServiceRegistryImpl
    implements ServiceRegistry
{
    private final BundleContext bundleContext;

    public ServiceRegistryImpl( final BundleContext bundleContext )
    {
        this.bundleContext = requireNonNull( bundleContext );
    }

    @Override
    public <T> ServiceRef<T> getService( final Class<T> type )
    {
        return new ServiceRefImpl<>( type, this.bundleContext );
    }
}
