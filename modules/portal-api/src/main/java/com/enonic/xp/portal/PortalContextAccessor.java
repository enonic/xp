package com.enonic.xp.portal;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;

@Beta
public final class PortalContextAccessor
{
    private final static ThreadLocal<PortalContext> CURRENT = new ThreadLocal<>();

    public static PortalContext get()
    {
        return CURRENT.get();
    }

    public static void set( final PortalContext context )
    {
        CURRENT.set( context );
    }

    public static void remove()
    {
        CURRENT.remove();
    }

    public static PortalContext get( final HttpServletRequest req )
    {
        return (PortalContext) req.getAttribute( PortalContext.class.getName() );
    }

    public static void set( final HttpServletRequest req, final PortalContext context )
    {
        req.setAttribute( PortalContext.class.getName(), context );
    }
}
