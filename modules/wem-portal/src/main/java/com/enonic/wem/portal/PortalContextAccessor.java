package com.enonic.wem.portal;

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
}
