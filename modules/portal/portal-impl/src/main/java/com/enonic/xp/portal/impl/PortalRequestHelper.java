package com.enonic.xp.portal.impl;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

public final class PortalRequestHelper
{
    private PortalRequestHelper()
    {
    }

    public static boolean isSiteBase( final WebRequest webRequest )
    {
        return webRequest instanceof PortalRequest portalRequest && portalRequest.getBaseUri() != null &&
            ( portalRequest.getBaseUri().equals( "/site" ) || portalRequest.getBaseUri().startsWith( "/admin/site/" ) );
    }

    public static String getSiteRelativePath( final PortalRequest portalRequest )
    {
        final ContentPath contentPath = portalRequest.getContentPath();

        if ( contentPath == null || contentPath.isRoot() )
        {
            return "/";
        }

        if ( portalRequest.getMode() != RenderMode.EDIT )
        {
            return siteRelativePath( portalRequest.getSite(), contentPath );
        }

        final Content content = portalRequest.getContent();
        if ( content == null )
        {
            return contentPath.toString();
        }
        else if ( content.getPath().isRoot() )
        {
            return "/";
        }
        else
        {
            return siteRelativePath( portalRequest.getSite(), content.getPath() );
        }
    }

    public static Content getContentOrElseThrow( final PortalRequest portalRequest )
    {
        final Content content = portalRequest.getContent();
        if ( content != null )
        {
            if ( content.getPath().isRoot() ||
                !content.getPermissions().isAllowedFor( ContextAccessor.current().getAuthInfo().getPrincipals(), Permission.READ ) )
            {
                throw WebException.forbidden( String.format( "You don't have permission to access [%s]", portalRequest.getContentPath() ) );
            }
            return content;
        }
        else
        {
            throw WebException.notFound( String.format( "Page [%s] not found", portalRequest.getContentPath() ) );
        }
    }

    public static Site getSiteOrElseThrow( final PortalRequest portalRequest )
    {
        if ( portalRequest.getSite() != null )
        {
            return portalRequest.getSite();
        }
        else
        {
            throw WebException.notFound( String.format( "Site for [%s] not found", portalRequest.getContentPath() ) );
        }
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
