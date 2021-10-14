package com.enonic.xp.portal.impl.handler.mapping;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.site.Site;

@Component(service = ContentResolver.class)
public class ContentResolver
{
    private final ContentService contentService;

    @Activate
    public ContentResolver( @Reference final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public ContentResolverResult resolve( final PortalRequest request )
    {
        final ContentPath contentPath = request.getContentPath();

        final Content content;
        final Site site;
        final String path;
        if ( request.getMode() == RenderMode.EDIT )
        {
            final ContentId contentId = ContentId.from( contentPath.toString().substring( 1 ) );
            content = this.contentService.getById( contentId );
            site = this.contentService.getNearestSite( contentId );
            if ( site == null )
            {
                return null;
            }
            path = content.getPath().toString();
        }
        else
        {
            site = this.contentService.findNearestSiteByPath( contentPath );
            if ( site == null )
            {
                return null;
            }
            content = getContentByPath( contentPath );
            path = contentPath.toString();
        }

        final String siteRelativePath = normalizePath( path.substring( site.getPath().toString().length() ) );
        return new ContentResolverResult( content, site, siteRelativePath );
    }

    private static String normalizePath( final String path )
    {
        return path.isEmpty() ? "/" : path;
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        try
        {
            return this.contentService.getByPath( contentPath );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }
}
