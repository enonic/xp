package com.enonic.wem.portal.dispatch;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.sun.jersey.api.core.ResourceContext;

@Path("/{workspace}/{mode}")
public class DispatcherResource
{
    private String workspace;

    private String mode;

    private ResourceContext resourceContext;

    @PathParam("workspace")
    public void setWorkspace( final String workspace )
    {
        this.workspace = workspace;
    }

    @PathParam("mode")
    public void setMode( final String mode )
    {
        this.mode = mode;
    }

    @Context
    public void setResourceContext( final ResourceContext resourceContext )
    {
        this.resourceContext = resourceContext;
    }

    @Path("{space}")
    public PageResource handle( @PathParam("space") String space )
    {
        final PageResource resource = this.resourceContext.getResource( PageResource.class );
        resource.setPath( "/" );
        return resource;
    }
}
