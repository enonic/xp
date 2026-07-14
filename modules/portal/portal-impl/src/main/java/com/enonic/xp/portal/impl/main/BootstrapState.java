package com.enonic.xp.portal.impl.main;

import com.enonic.xp.app.ApplicationKey;

/**
 * Tracks per-application bootstrap: an application with a {@code main.js} is bootstrapped when
 * its execution completes (successfully or not). Controller executions await bootstrap so
 * initialization code in {@code main.js} — repository setup, listener registration — observably
 * happens before any controller runs (<a href="https://github.com/enonic/xp/issues/7821">#7821</a>).
 */
public interface BootstrapState
{
    /**
     * Blocks until the application's {@code main.js} execution completes. Returns immediately
     * for applications without {@code main.js} (or not yet activated). Bounded: a hanging
     * bootstrap logs a warning and lets callers proceed instead of damming the application
     * forever.
     */
    void awaitBootstrapped( ApplicationKey key );
}
