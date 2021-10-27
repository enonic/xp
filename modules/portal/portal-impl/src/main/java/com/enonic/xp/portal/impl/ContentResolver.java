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

        if ( request.getMode() == RenderMode.EDIT )
        {
            return resolveInEditMode( contentPath );
        }
        else
        {
            return resolveInNonEditMode( contentPath );
        }
    }

    private ContentResolverResult resolveInNonEditMode( final ContentPath contentPath )
    {
        Content content = getContentByPath( contentPath );
        final Site site = callAsContentAdmin( () -> this.contentService.findNearestSiteByPath( contentPath ) );
        if ( site == null )
        {
            return ContentResolverResult.noSiteFound( content, contentPath.toString() );
        }
        else
        {
            return ContentResolverResult.build( content, site, contentPath.toString(), contentPath.toString() );
        }
    }

    private ContentResolverResult resolveInEditMode( final ContentPath contentPath )
    {
        final Site site;

        Content content = getContentById( ContentId.from( contentPath.toString().substring( 1 ) ) );

        if ( content == null )
        {
            content = getContentByPath( contentPath );
        }

        if ( content == null )
        {
            return ContentResolverResult.nothingFound( contentPath.toString() );
        }
        else
        {
            final ContentId contentId = content.getId();
            site = callAsContentAdmin( () -> this.contentService.getNearestSite( contentId ) );
        }

        if ( site == null )
        {
            return ContentResolverResult.noSiteFound( content, contentPath.toString() );
        }
        else
        {
            return ContentResolverResult.build( content, site, content.getPath().toString(), contentPath.toString() );
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
        catch ( final Exception e )
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
