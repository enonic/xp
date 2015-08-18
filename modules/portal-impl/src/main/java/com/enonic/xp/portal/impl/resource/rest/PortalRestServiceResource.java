package com.enonic.xp.portal.impl.resource.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;
import com.enonic.xp.portal.rest.PortalRestService;
import com.enonic.xp.portal.rest.PortalRestServiceNotFoundException;
import com.enonic.xp.site.Site;

public class PortalRestServiceResource
    extends BaseSubResource
{
    @Context
    protected HttpServletRequest httpServletRequest;

    @Path("{portalRestService}")
    public PortalRestService portalRestService( @PathParam("portalRestService") final String portalRestServiceName )
    {
        PortalRequest portalRequest = PortalRequestAccessor.get( httpServletRequest );
        if ( portalRequest == null )
        {
            portalRequest = new PortalRequest();
            PortalRequestAccessor.set( httpServletRequest, portalRequest );
        }
        final Content content = getContent( this.contentPath.toString() );
        final Site site = content != null ? getSite( content ) : null;
        portalRequest.setContent( content );
        portalRequest.setSite( site );

        final PortalRestService portalRestService =
            this.services.getPortalRestServiceRegistry().getPortalRestService( portalRestServiceName );
        if ( portalRestService == null )
        {
            throw new PortalRestServiceNotFoundException( portalRestServiceName );
        }
        return portalRestService;
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
