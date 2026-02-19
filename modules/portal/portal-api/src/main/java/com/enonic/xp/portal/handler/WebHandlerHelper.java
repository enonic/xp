package com.enonic.xp.portal.handler;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.dispatch.DispatchConstants;

public final class WebHandlerHelper
{
    public static void checkAdminAccess( final WebRequest webRequest )
    {
        if ( isLiveMode( webRequest ) )
        {
            return;
        }
        checkAdminLoginRole( webRequest );
    }

    private static boolean isLiveMode( final WebRequest webRequest )
    {
        return webRequest instanceof PortalRequest portalRequest && RenderMode.LIVE == portalRequest.getMode();
    }

    public static void checkAdminLoginRole( final WebRequest webRequest )
    {
        if ( webRequest.getRawRequest().isUserInRole( RoleKeys.ADMIN_LOGIN_ID ) ||
            webRequest.getRawRequest().isUserInRole( RoleKeys.ADMIN_ID ) )
        {
            return;
        }
        throw WebException.forbidden( "You don't have permission to access this resource" );
    }

    @NullMarked
    public static String findApiPath( final WebRequest req, final String api )
    {
        final String connector =
            req.getRawRequest() != null ? (String) req.getRawRequest().getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) : null;

        final String prefix;
        final String basePath;
        if ( DispatchConstants.API_CONNECTOR.equals( connector ) )
        {
            prefix = "/";
            basePath = req.getRawPath();
        }
        else
        {
            if ( req.getEndpointPath() == null )
            {
                prefix = "/api/";
                basePath = req.getBasePath();
            }
            else
            {
                prefix = "/";
                basePath = req.getEndpointPath();
            }
        }
        return findPath( prefix, api, basePath );
    }

    private static String findPath( final String prefix, final String api, final String basePath )
    {
        if ( !basePath.startsWith( prefix ) || !basePath.regionMatches( prefix.length(), api, 0, api.length() ) )
        {
            throw new IllegalArgumentException( "Unexpected API path: " + basePath );
        }

        final int prefixLength = api.length() + prefix.length();
        if ( basePath.length() == prefixLength )
        {
            return "";
        }
        if ( basePath.charAt( prefixLength ) != '/' )
        {
            throw new IllegalArgumentException( "Unexpected API path: " + basePath );
        }
        return basePath.substring( prefixLength );
    }

    private WebHandlerHelper()
    {
    }
}
