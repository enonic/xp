package com.enonic.xp.portal.impl;

import java.util.concurrent.Callable;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;

public class ContentResolver
{
    private final ContentService contentService;

    public ContentResolver( final ContentService contentService )
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
            content = getContentById( contentId );
            if ( content == null )
            {
                return ContentResolverResult.nothingFound( contentId.toString() );
            }
            site = callAsContentAdmin( () -> this.contentService.getNearestSite( contentId ) );
            if ( site == null )
            {
                return ContentResolverResult.noSiteFound( content, contentId.toString() );
            }
            else
            {
                return ContentResolverResult.build( content, site, content.getPath().toString(), contentId.toString() );
            }
        }
        else
        {
            content = getContentByPath( contentPath );
            site = callAsContentAdmin( () -> this.contentService.findNearestSiteByPath( contentPath ) );
            if ( site == null )
            {
                return ContentResolverResult.build( content, null, null, contentPath.toString() );
            }
            else
            {
                return ContentResolverResult.build( content, site, contentPath.toString(),
                                                    contentPath.toString() );
            }
        }
    }

    private Content getContentById( final ContentId contentId )
    {
        try
        {
            final Content content = this.contentService.getById( contentId );
            if ( ContentPath.ROOT.equals( content.getPath() ) )
            {
                return null;
            }
            else
            {
                return content;
            }
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        if ( contentPath.equals( ContentPath.ROOT ) )
        {
            return null;
        }
        try
        {
            return this.contentService.getByPath( contentPath );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private <T> T callAsContentAdmin( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        return ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.CONTENT_MANAGER_ADMIN ).build() )
            .build()
            .callWith( callable );
    }
}
