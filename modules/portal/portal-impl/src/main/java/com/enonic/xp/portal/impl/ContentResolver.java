package com.enonic.xp.portal.impl;

import java.util.Optional;
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

        final boolean contentExists = content != null || contentExistsByPath( contentPath );

        final Site site = callAsContentAdmin( () -> this.contentService.findNearestSiteByPath( contentPath ) );

        return new ContentResolverResult( content, contentExists, site, siteRelativePath( site, contentPath ), contentPath.toString() );
    }

    private ContentResolverResult resolveInEditMode( final ContentPath contentPath )
    {
        final ContentId contentId = ContentId.from( contentPath.toString().substring( 1 ) );

        final Content content = Optional.ofNullable( getContentById( contentId ) ).orElseGet( () -> getContentByPath( contentPath ) );

        final boolean contentExists = content != null || contentExistsById( contentId ) || contentExistsByPath( contentPath );

        final Site site = content != null ? callAsContentAdmin( () -> this.contentService.getNearestSite( content.getId() ) ) : null;

        return new ContentResolverResult( content, contentExists, site,
                                          siteRelativePath( site, content == null ? null : content.getPath() ), contentPath.toString() );
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

    private boolean contentExistsById( final ContentId contentId )
    {
        return this.contentService.contentExists( contentId );
    }

    private boolean contentExistsByPath( final ContentPath contentPath )
    {
        return !ContentPath.ROOT.equals( contentPath ) && this.contentService.contentExists( contentPath );
    }

    private static <T> T callAsContentAdmin( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        return ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.CONTENT_MANAGER_ADMIN ).build() )
            .build()
            .callWith( callable );
    }

    private static String siteRelativePath( final Site site, final ContentPath contentPath )
    {
        if ( site == null || contentPath == null)
        {
            return null;
        }
        if ( site.getPath().equals( contentPath ) )
        {
            return "/";
        }
        return contentPath.toString().substring( site.getPath().toString().length() );
    }
}
