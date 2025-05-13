package com.enonic.xp.web.vhost;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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
