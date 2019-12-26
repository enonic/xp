package com.enonic.xp.portal;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PortalRequestAccessor
{
    private final static ThreadLocal<PortalRequest> CURRENT = new ThreadLocal<>();

    public static PortalRequest get()
    {
        return CURRENT.get();
    }

    public static void set( final PortalRequest portalRequest )
    {
        CURRENT.set( portalRequest );
    }

    public static void remove()
    {
        CURRENT.remove();
    }

    public static PortalRequest get( final HttpServletRequest req )
    {
        return (PortalRequest) req.getAttribute( PortalRequest.class.getName() );
    }

    public static void set( final HttpServletRequest req, final PortalRequest portalRequest )
    {
        req.setAttribute( PortalRequest.class.getName(), portalRequest );
    }
}
