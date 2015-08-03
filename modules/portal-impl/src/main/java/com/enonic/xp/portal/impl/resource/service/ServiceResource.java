package com.enonic.xp.portal.impl.resource.service;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.site.Site;

public final class ServiceResource
    extends BaseSubResource
{
    @Path("{application}/{service}")
    public ServiceControllerResource controller( @PathParam("application") final String application,
                                                 @PathParam("service") final String service )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( application );
        final ResourceKey scriptDir = ResourceKey.from( applicationKey, "site/services/" + service );

        final ServiceControllerResource resource = initResource( new ServiceControllerResource() );
        resource.scriptDir = scriptDir;

        resource.content = getContent( this.contentPath.toString() );
        resource.site = resource.content != null ? getSite( resource.content ) : null;

        return resource;
    }

    private Content getContent( final String contentSelector )
    {
        final boolean inEditMode = ( this.mode == RenderMode.EDIT );
        if ( inEditMode )
        {
            final ContentId contentId = ContentId.from( contentSelector.substring( 1 ) );
            final Content contentById = getContentById( contentId );
            if ( contentById != null )
            {
                return contentById;
            }
        }

        final ContentPath contentPath = ContentPath.from( contentSelector ).asAbsolute();
        final Content content = getContentByPath( contentPath );
        if ( content != null )
        {
            return content;
        }

        return null;
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        try
        {
            return this.services.getContentService().getByPath( contentPath );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private Content getContentById( final ContentId contentId )
    {
        try
        {
            return this.services.getContentService().getById( contentId );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private Site getSite( final Content content )
    {
        return this.services.getContentService().getNearestSite( content.getId() );
    }
}
