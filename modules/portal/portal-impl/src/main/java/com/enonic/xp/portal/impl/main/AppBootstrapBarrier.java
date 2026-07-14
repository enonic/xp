package com.enonic.xp.portal.impl.main;

import com.enonic.xp.app.ApplicationKey;

/**
 * Consumer seam over the per-application bootstrap {@link org.osgi.service.condition.Condition}
 * published by {@link MainExecutor}. Controllers await it so that {@code main.js} initialization
 * observably completes before any controller runs
 * (<a href="https://github.com/enonic/xp/issues/7821">#7821</a>).
 * <p>
 * The bootstrap state itself lives in the OSGi service registry as a string-identified Condition
 * — this interface only exists so consumers can be unit-tested without a registry (a no-op
 * {@code key -> {}} in tests).
 */
@FunctionalInterface
public interface AppBootstrapBarrier
{
    /**
     * Blocks until the application's bootstrap Condition is present, or returns immediately if it
     * already is. Bounded — a bootstrap that never completes fails open (the caller proceeds)
     * rather than blocking forever.
     */
    void await( ApplicationKey applicationKey );
}
