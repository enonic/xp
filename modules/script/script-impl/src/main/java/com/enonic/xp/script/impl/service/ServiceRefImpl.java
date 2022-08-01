package com.enonic.xp.script.impl.service;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

final class ServiceRefImpl<T>
    implements ServiceRef<T>
{
    private final Class<T> type;

    private final BundleContext bundleContext;

    private final String filter;

    ServiceRefImpl( final Class<T> type, final BundleContext bundleContext, final String filter )
    {
        this.type = type;
        this.bundleContext = bundleContext;
        this.filter = filter;
    }

    @Override
    public T get()
    {
        final T service = findService();
        if ( service != null )
        {
            return service;
        }

        throw new IllegalArgumentException( "Service [" + this.type.getName() + "] not found" );
    }


    private T findService()
    {
        final Collection<ServiceReference<T>> refs;
        try
        {
            refs = this.bundleContext.getServiceReferences( this.type, filter );
        }
        catch ( InvalidSyntaxException e )
        {
            return null;
        }

        if ( refs.isEmpty() )
        {
            return null;
        }

        return this.bundleContext.getService( refs.stream().findAny().get() );
    }
}
