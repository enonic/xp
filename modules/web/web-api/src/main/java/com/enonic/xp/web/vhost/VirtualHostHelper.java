package com.enonic.xp.web.vhost;

import jakarta.servlet.http.HttpServletRequest;


public final class VirtualHostHelper
{
    private static final String KEY = VirtualHost.class.getName();

    private static final String REQUESTED_PATH_KEY = VirtualHost.class.getName() + ".requestedPath";

    public static VirtualHost getVirtualHost( final HttpServletRequest req )
    {
        return (VirtualHost) req.getAttribute( KEY );
    }

    public static void setVirtualHost( final HttpServletRequest req, final VirtualHost vhost )
    {
        req.setAttribute( KEY, vhost );
    }

    /**
     * The path the request came to as the client sent it, percent-encoded as it was and before
     * the virtual host mapping rewrote it: the path of the URL the client holds. Null until the
     * virtual host filter has run, and for a path that is not in canonical form (dot segments,
     * empty segments, path parameters), since the routing resolves those and the path would name
     * another resource than the one the request reaches.
     */
    public static String getRequestedPath( final HttpServletRequest req )
    {
        return (String) req.getAttribute( REQUESTED_PATH_KEY );
    }

    public static void setRequestedPath( final HttpServletRequest req, final String path )
    {
        req.setAttribute( REQUESTED_PATH_KEY, path );
    }

    private VirtualHostHelper()
    {
    }
}
