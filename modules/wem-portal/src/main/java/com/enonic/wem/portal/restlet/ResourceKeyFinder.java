package com.enonic.wem.portal.restlet;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;

import com.google.inject.Injector;
import com.google.inject.Key;

final class ResourceKeyFinder<T extends ServerResource>
    extends Finder
{
    private final Injector injector;

    private final Key<T> key;

    public ResourceKeyFinder( final Injector injector, final Key<T> key )
    {
        this.injector = injector;
        this.key = key;
    }

    @Override
    public ServerResource create( final Request request, final Response response )
    {
        return this.injector.getInstance( this.key );
    }
}
