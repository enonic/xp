package com.enonic.wem.portal.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.portal.exception.ContentNotFoundException;
import com.enonic.wem.portal.service.ContentService;

public class SpaceResource
    extends AbstractResource
{
    private ContentService contentService;

    @GET
    @Produces("text/plain")
    public String doGet()
    {
        contentService.getContent( getPortalRequest() );

        final Content content = contentService.getContent( getPortalRequest() );

        if ( content != null )
        {
            return content.toString();
        }

        throw new ContentNotFoundException(
            "Content with path " + getPortalRequest().getPortalRequestPath().getPathAsString() + " not found" );
    }

    @Path("{name}")
    public SpaceResource handlePathElement( @PathParam("name") String name )
    {
        final SpaceResource resource = this.resourceContext.getResource( SpaceResource.class );
        getPortalRequest().appendPath( name );

        return resource;
    }

    @Path("_")
    public UnderscoreDispatcherResource handleUnderscore()
    {
        return this.resourceContext.getResource( UnderscoreDispatcherResource.class );
    }

    @Inject
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
