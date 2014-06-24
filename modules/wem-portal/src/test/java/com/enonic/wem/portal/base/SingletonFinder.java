package com.enonic.wem.portal.base;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;

final class SingletonFinder
    extends Finder
{
    private final ServerResource resource;

    public SingletonFinder( final ServerResource resource )
    {
        this.resource = resource;
    }

    @Override
    public ServerResource create( final Request request, final Response response )
    {
        return this.resource;
    }
}
