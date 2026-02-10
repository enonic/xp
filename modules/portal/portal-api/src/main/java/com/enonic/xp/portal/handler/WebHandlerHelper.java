package com.enonic.xp.portal.handler;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

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

    private WebHandlerHelper()
    {
    }
}
