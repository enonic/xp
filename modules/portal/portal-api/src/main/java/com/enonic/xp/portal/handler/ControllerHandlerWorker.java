package com.enonic.xp.portal.handler;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.site.Site;

public abstract class ControllerHandlerWorker
    extends PortalHandlerWorker<PortalRequest>
{
    protected ContentService contentService;

    public ControllerHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    protected final String getContentSelector()
    {
        return this.request.getContentPath().toString();
    }

    protected final Content getContent( final String contentSelector )
    {
        final Content content = getContentOrNull( contentSelector );
        if ( content != null )
        {
            return content;
        }

        if ( contentExists( contentSelector ) )
        {
            throw forbidden( "You don't have permission to access [%s]", contentSelector );
        }
        else
        {
            throw notFound( "Page [%s] not found", contentSelector );
        }
    }

    protected final Content getContentOrNull( final String contentSelector )
    {
        final boolean inEditMode = ( this.request.getMode() == RenderMode.EDIT );
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
        return getContentByPath( contentPath );
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        if ( ContentPath.ROOT.equals( contentPath ) )
        {
            return null;
        }
        try
        {
            return this.contentService.getByPath( contentPath );
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
            return this.contentService.getById( contentId );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    protected final Site getSite( final Content content )
    {
        final Site site = getSiteOrNull( content );
        if ( site != null )
        {
            return site;
        }

        throw notFound( "Site for content [%s] not found", content.getPath() );
    }

    protected final Site getSiteOrNull( final Content content )
    {
        return content != null ? this.contentService.getNearestSite( content.getId() ) : null;
    }

    private boolean contentExists( final String contentSelector )
    {
        final ContentId contentId = ContentId.from( contentSelector.substring( 1 ) );
        final ContentPath contentPath = ContentPath.from( contentSelector ).asAbsolute();
        return this.contentService.contentExists( contentId ) || (!ContentPath.ROOT.equals( contentPath ) && this.contentService.contentExists( contentPath ));
    }

    public final void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
