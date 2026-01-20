package com.enonic.xp.script.impl.service;

import java.util.Objects;

import org.osgi.framework.BundleContext;

public final class ServiceRegistryImpl
    implements ServiceRegistry
{
    private final BundleContext bundleContext;

    public ServiceRegistryImpl( final BundleContext bundleContext )
    {
        this.bundleContext = Objects.requireNonNull( bundleContext );
    }

    @Override
    public <T> ServiceRef<T> getService( final Class<T> type )
    {
        return new ServiceRefImpl<>( type, this.bundleContext );
    }
}
