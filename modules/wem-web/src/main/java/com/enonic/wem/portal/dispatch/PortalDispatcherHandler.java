package com.enonic.wem.portal.dispatch;

import com.enonic.wem.portal.AbstractResource;

// @Path("/{workspace}/{mode}")
public class PortalDispatcherHandler
    extends AbstractResource
{
    /*
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
    public SpaceDispatcherHandler handle( @PathParam("spaceName") String spaceName )
    {
        final Space space = validateSpace( spaceName );

        final SpaceDispatcherHandler resource = this.resourceContext.getResource( SpaceDispatcherHandler.class );

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
    */
}
