package com.enonic.xp.portal.impl;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.WebRequest;

public final class PortalRequestHelper
{
    private PortalRequestHelper()
    {
    }

    public static boolean isSiteBase( final WebRequest webRequest )
    {
        return webRequest instanceof PortalRequest portalRequest &&
            ( portalRequest.getBaseUri().equals( "/site" ) || portalRequest.getBaseUri().startsWith( "/admin/site/" ) );
    }
}
