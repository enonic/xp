package com.enonic.wem.portal.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.wem.portal.AbstractResource;

public class StaticResource
    extends AbstractResource
{
    @GET
    public String getStaticResource()
    {
        return "This is a static resource, path: " + getStaticRequest().getRelativePathAsString();
    }

    @Path("{name}")
    public StaticResource handlePathElement( @PathParam("name") String name )
    {
        final StaticResource resource = this.resourceContext.getResource( StaticResource.class );

        final StaticResourceRequest staticResourceRequest = getStaticRequest();

        staticResourceRequest.appendPath( name );

        return resource;
    }

    private StaticResourceRequest getStaticRequest()
    {
        return this.resourceContext.getResource( StaticResourceRequest.class );
    }


}
