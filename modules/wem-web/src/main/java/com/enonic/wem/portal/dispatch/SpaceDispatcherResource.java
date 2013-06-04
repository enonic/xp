package com.enonic.wem.portal.dispatch;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.enonic.wem.portal.AbstractResource;
import com.enonic.wem.portal.content.PageResource;

public class SpaceDispatcherResource
    extends AbstractResource
{
    @GET
    @Produces("text/plain")
    public String doGet()
    {
        return this.resourceContext.getResource( PageResource.class ).getPage();
    }

    @Path("{name}")
    public SpaceDispatcherResource handlePathElement( @PathParam("name") String name )
    {
        final SpaceDispatcherResource resource = this.resourceContext.getResource( SpaceDispatcherResource.class );
        getPortalRequest().appendPath( name );

        return resource;
    }

    @Path("_")
    public UnderscoreDispatcherResource handleUnderscore()
    {
        return this.resourceContext.getResource( UnderscoreDispatcherResource.class );
    }

}
