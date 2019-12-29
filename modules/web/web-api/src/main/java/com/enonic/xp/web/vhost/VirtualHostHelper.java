package com.enonic.xp.web.vhost;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class VirtualHostHelper
{
    private final static String KEY = VirtualHost.class.getName();

    public static boolean hasVirtualHost( final HttpServletRequest req )
    {
        return getVirtualHost( req ) != null;
    }

    public static VirtualHost getVirtualHost( final HttpServletRequest req )
    {
        return (VirtualHost) req.getAttribute( KEY );
    }

    public static void setVirtualHost( final HttpServletRequest req, final VirtualHost vhost )
    {
        req.setAttribute( KEY, vhost );
    }
}
