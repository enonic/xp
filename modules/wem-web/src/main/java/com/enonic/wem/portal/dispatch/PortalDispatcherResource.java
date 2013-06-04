package com.enonic.wem.portal.dispatch;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.portal.AbstractResource;

@Path("/{workspace}/{mode}")
public class PortalDispatcherResource
    extends AbstractResource
{
    private SpaceService spaceService;

    @PathParam("workspace")
    public void setWorkspace( final String workspace )
    {
        getPortalRequest().setWorkspace( workspace );
    }

    @PathParam("mode")
    public void setMode( final String mode )
    {
        getPortalRequest().setMode( mode );
    }

    @Path("space/{spaceName}")
    public SpaceDispatcherResource handle( @PathParam("spaceName") String spaceName )
    {
        final Space space = validateSpace( spaceName );

        final SpaceDispatcherResource resource = this.resourceContext.getResource( SpaceDispatcherResource.class );

        getPortalRequest().createPortalRequestPath( space.getName() );

        return resource;
    }

    private Space validateSpace( final String spaceName )
    {
        final Space space = spaceService.getSpace( spaceName );

        if ( space == null )
        {
            throw new SpaceNotFoundException( spaceName );
        }

        return space;
    }

    @Inject
    public void setSpaceService( final SpaceService spaceService )
    {
        this.spaceService = spaceService;
    }
}
