package com.enonic.wem.portal.dispatch;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.ResourceContext;

public class PageResource
{
    private String path;

    private ResourceContext resourceContext;

    private HttpHeaders httpHeaders;

    private UriInfo uriInfo;

    public void setPath( final String path )
    {
        this.path = path;
    }

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

    @GET
    public String doGet()
    {
        return "Handle page: " + path;
    }

    @Path("{name}")
    public PageResource handleSubPage( @PathParam("name") String name )
    {
        final PageResource resource = this.resourceContext.getResource( PageResource.class );

        if ( this.path.endsWith( "/" ) )
        {
            resource.setPath( this.path + name );
        }
        else
        {
            resource.setPath( this.path + "/" + name );
        }

        return resource;
    }

    @Path("_")
    public UnderscoreResource handleUnderscore()
    {
        return this.resourceContext.getResource( UnderscoreResource.class );
    }
}
