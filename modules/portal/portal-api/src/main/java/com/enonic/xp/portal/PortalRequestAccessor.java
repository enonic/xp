package com.enonic.xp.portal;

import java.util.function.Supplier;

import jakarta.servlet.http.HttpServletRequest;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class PortalRequestAccessor
{
    private static final ScopedValue<PortalRequest> CURRENT = ScopedValue.newInstance();

    // Backs the deprecated imperative set/remove. New code must bind through runWith/callWith.
    private static final ThreadLocal<@Nullable PortalRequest> LEGACY = new ThreadLocal<>();

    public static @Nullable PortalRequest get()
    {
        return CURRENT.isBound() ? CURRENT.get() : LEGACY.get();
    }

    /**
     * Binds the given request to the current thread for the duration of the runnable, restoring any previous binding when it returns.
     */
    public static void runWith( final PortalRequest portalRequest, final Runnable runnable )
    {
        ScopedValue.where( CURRENT, portalRequest ).run( runnable );
    }

    /**
     * Binds the given request to the current thread for the duration of the supplier, restoring any previous binding when it returns.
     */
    public static <R extends @Nullable Object> R callWith( final PortalRequest portalRequest, final Supplier<R> supplier )
    {
        return ScopedValue.where( CURRENT, portalRequest ).call( supplier::get );
    }

    /**
     * @deprecated Use {@link #runWith(PortalRequest, Runnable)} or {@link #callWith(PortalRequest, Supplier)}.
     */
    @Deprecated
    public static void set( final @Nullable PortalRequest portalRequest )
    {
        LEGACY.set( portalRequest );
    }

    /**
     * @deprecated Use {@link #runWith(PortalRequest, Runnable)} or {@link #callWith(PortalRequest, Supplier)}.
     */
    @Deprecated
    public static void remove()
    {
        LEGACY.remove();
    }

    public static @Nullable PortalRequest get( final HttpServletRequest req )
    {
        return (PortalRequest) req.getAttribute( PortalRequest.class.getName() );
    }

    public static void set( final HttpServletRequest req, final PortalRequest portalRequest )
    {
        req.setAttribute( PortalRequest.class.getName(), portalRequest );
    }

    private PortalRequestAccessor()
    {
    }
}
