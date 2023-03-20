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
import com.enonic.xp.security.acl.Permission;
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
        final Content content = callAsContentAdmin( () -> getContentByPath( contentPath ) );

        final Site site = callAsContentAdmin( () -> this.contentService.findNearestSiteByPath( contentPath ) );

        final String siteRelativePath = siteRelativePath( site, contentPath );
        return new ContentResolverResult( visibleContent( content ), content != null, site, siteRelativePath, contentPath.toString() );
    }

    private ContentResolverResult resolveInEditMode( final ContentPath contentPath )
    {
        final String contentPathString = contentPath.toString();

        final ContentId contentId = tryConvertToContentId( contentPathString );

        final Content contentById = contentId != null ? callAsContentAdmin( () -> getContentById( contentId ) ) : null;

        final Content content = contentById != null ? contentById : callAsContentAdmin( () -> this.getContentByPath( contentPath ) );

        final Site site = getNearestSite( content );

        final String siteRelativePath = siteRelativePath( site, content != null ? content.getPath() : contentPath );
        return new ContentResolverResult( visibleContent( content ), content != null, site, siteRelativePath, contentPathString );
    }

    private Site getNearestSite( final Content content )
    {
        if ( content != null )
        {
            if ( ContentPath.ROOT.equals( content.getPath() ) )
            {
                return null;
            }
            else
            {
                return content.isSite()
                    ? (Site) content
                    : callAsContentAdmin( () -> this.contentService.getNearestSite( content.getId() ) );
            }
        }
        else
        {
            return null;
        }
    }

    private static ContentId tryConvertToContentId( final String contentPathString )
    {
        try
        {
            return ContentId.from( contentPathString.substring( 1 ) );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private Content visibleContent( final Content content )
    {
        return content == null || ContentPath.ROOT.equals( content.getPath() ) ||
            !content.getPermissions().isAllowedFor( ContextAccessor.current().getAuthInfo().getPrincipals(), Permission.READ )
            ? null
            : content;
    }

    private Content getContentById( final ContentId contentId )
    {
        try
        {
            return this.contentService.getById( contentId );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
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
        if ( site == null )
        {
            return contentPath.toString();
        }
        else if ( site.getPath().equals( contentPath ) )
        {
            return "/";
        }
        else
        {
            return contentPath.toString().substring( site.getPath().toString().length() );
        }
    }
}
