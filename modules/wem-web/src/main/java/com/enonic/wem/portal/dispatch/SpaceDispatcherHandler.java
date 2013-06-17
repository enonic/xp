package com.enonic.wem.portal.dispatch;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.enonic.wem.portal.AbstractResource;
import com.enonic.wem.portal.content.PageRequestHandler;

public class SpaceDispatcherHandler
    extends AbstractResource
{
    @GET
    @Produces("text/plain")
    public String doGet()
    {
        return this.resourceContext.getResource( PageRequestHandler.class ).getPage();
    }

    @Path("{pathElement}")
    public SpaceDispatcherHandler handlePathElement( @PathParam("pathElement") String pathElement )
    {
        final SpaceDispatcherHandler resource = this.resourceContext.getResource( SpaceDispatcherHandler.class );
        getPortalRequest().appendPath( pathElement );

        return resource;
    }

    @Path("_")
    public UnderscoreDispatcherResource handleUnderscore()
    {
        return this.resourceContext.getResource( UnderscoreDispatcherResource.class );
    }

}
