package com.enonic.xp.portal.handler;

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

    public static String findApiPath( final WebRequest req, final String api )
    {
        final String endpointPath = req.getEndpointPath();
        if ( endpointPath != null )
        {
            final int prefixLength = api.length() + 1;
            if ( endpointPath.length() == prefixLength )
            {
                return "";
            }
            if ( endpointPath.charAt( prefixLength ) != '/' )
            {
                throw WebException.notFound( "Unexpected endpoint path: " + endpointPath );
            }
            return endpointPath.substring( prefixLength );
        }
        else
        {
            final String rawPath = req.getRawPath();
            final String connector =
                req.getRawRequest() != null ? (String) req.getRawRequest().getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) : null;

            if ( DispatchConstants.API_CONNECTOR.equals( connector ) )
            {
                final int prefixLength = api.length() + 1;
                if ( rawPath.length() == prefixLength )
                {
                    return "";
                }
                if ( rawPath.charAt( prefixLength ) != '/' )
                {
                    throw WebException.notFound( "Unexpected API path: " + rawPath );
                }
                return rawPath.substring( prefixLength );
            }
            else
            {
                final String basePath = req.getBasePath();
                final int prefixLength = api.length() + 5;
                if ( basePath.length() == prefixLength )
                {
                    return "";
                }
                if ( basePath.charAt( prefixLength ) != '/' )
                {
                    throw WebException.notFound( "Unexpected API path: " + basePath );
                }
                return basePath.substring( prefixLength );
            }
        }
    }

    private WebHandlerHelper()
    {
    }
}
