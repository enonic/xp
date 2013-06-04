package com.enonic.wem.portal;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.ResourceContext;

import com.enonic.wem.portal.dispatch.PortalRequest;

public class AbstractResource
{
    protected ResourceContext resourceContext;

    protected HttpHeaders httpHeaders;

    protected UriInfo uriInfo;

    @Context
    public void setResourceContext( final ResourceContext resourceContext )
    {
        this.resourceContext = resourceContext;
    }

    @Context
    public void setHttpHeaders( final HttpHeaders httpHeaders )
    {
        this.httpHeaders = httpHeaders;
    }

    @Context
    public void setUriInfo( final UriInfo uriInfo )
    {
        this.uriInfo = uriInfo;
    }

    public PortalRequest getPortalRequest()
    {
        return this.resourceContext.getResource( PortalRequest.class );
    }
}
