package com.enonic.xp.portal.impl.main;

import com.enonic.xp.app.ApplicationKey;

/**
 * Marks the dynamic extent of an application's {@code main.js} bootstrap on the current thread,
 * carrying the key of the application being bootstrapped. While it is active for an application,
 * {@code PortalScriptService} does not gate that same application's script executions on its
 * bootstrap Condition: {@code main.js} — and everything it triggers synchronously, such as a task
 * it submits — must run before the Condition is published (which only happens once {@code main.js}
 * returns), so a re-entrant execution on the bootstrap thread must never wait for it. A different
 * application's script is still gated: the scope is keyed on one application, not a global flag.
 */
public final class BootstrapScope
{
    private static final ScopedValue<ApplicationKey> BOOTSTRAPPING = ScopedValue.newInstance();

    private BootstrapScope()
    {
    }

    public static ApplicationKey current()
    {
        return BOOTSTRAPPING.isBound() ? BOOTSTRAPPING.get() : null;
    }

    public static void run( final ApplicationKey applicationKey, final Runnable runnable )
    {
        ScopedValue.where( BOOTSTRAPPING, applicationKey ).run( runnable );
    }
}
