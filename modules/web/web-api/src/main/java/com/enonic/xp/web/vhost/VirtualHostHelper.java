package com.enonic.xp.web.vhost;

import jakarta.servlet.http.HttpServletRequest;


public final class VirtualHostHelper
{
    private static final String KEY = VirtualHost.class.getName();

    public static VirtualHost getVirtualHost( final HttpServletRequest req )
    {
        return (VirtualHost) req.getAttribute( KEY );
    }

    public static void setVirtualHost( final HttpServletRequest req, final VirtualHost vhost )
    {
        req.setAttribute( KEY, vhost );
    }

    private VirtualHostHelper()
    {
    }
}
