package com.enonic.wem.portal.internal.restlet;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;

import com.google.common.base.Throwables;

public abstract class ResourceFactory<T extends ServerResource>
    extends Finder
{
    private final Class<T> type;

    public ResourceFactory( final Class<T> type )
    {
        this.type = type;
    }

    public final Class<T> getType()
    {
        return this.type;
    }

    protected abstract void configure( T instance );

    public final T newResource()
    {
        try
        {
            final T instance = this.type.newInstance();
            configure( instance );
            return instance;
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public final ServerResource create( final Request request, final Response response )
    {
        return newResource();
    }
}
