package com.enonic.xp.portal.impl.main;

/**
 * Marks the dynamic extent of an application's {@code main.js} bootstrap on the current thread.
 * While active, {@code PortalScriptService} does not gate script execution on that application's
 * bootstrap Condition: {@code main.js} — and everything it triggers synchronously, such as a task
 * it submits — must run before the Condition is published (which only happens once {@code main.js}
 * returns), so a re-entrant execution on the bootstrap thread must never wait for it.
 */
public final class BootstrapScope
{
    private static final ScopedValue<Boolean> ACTIVE = ScopedValue.newInstance();

    private BootstrapScope()
    {
    }

    public static boolean isActive()
    {
        return ACTIVE.isBound();
    }

    public static void run( final Runnable runnable )
    {
        ScopedValue.where( ACTIVE, Boolean.TRUE ).run( runnable );
    }
}
