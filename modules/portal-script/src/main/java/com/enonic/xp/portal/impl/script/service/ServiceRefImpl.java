package com.enonic.xp.portal.impl.script.service;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

final class ServiceRefImpl<T>
    implements ServiceRef<T>
{
    private final Class<T> type;

    private final BundleContext bundleContext;

    public ServiceRefImpl( final Class<T> type, final BundleContext bundleContext )
    {
        this.type = type;
        this.bundleContext = bundleContext;
    }

    @Override
    public T get()
    {
        final ServiceReference<T> ref = this.bundleContext.getServiceReference( this.type );
        return this.bundleContext.getService( ref );
    }
}
