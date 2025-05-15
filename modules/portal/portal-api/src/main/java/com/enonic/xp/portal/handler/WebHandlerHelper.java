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
        if ( !isLiveMode( webRequest ) && !hasAdminRight( webRequest ) )
        {
            throw WebException.forbidden( "You don't have permission to access this resource" );
        }
    }

    private static boolean isLiveMode( final WebRequest webRequest )
    {
        return webRequest instanceof PortalRequest && RenderMode.LIVE == ( (PortalRequest) webRequest ).getMode();
    }

    private static boolean hasAdminRight( final WebRequest webRequest )
    {
        return webRequest.getRawRequest().isUserInRole( RoleKeys.ADMIN_LOGIN_ID ) ||
            webRequest.getRawRequest().isUserInRole( RoleKeys.ADMIN_ID );
    }

    private WebHandlerHelper()
    {
    }
}
