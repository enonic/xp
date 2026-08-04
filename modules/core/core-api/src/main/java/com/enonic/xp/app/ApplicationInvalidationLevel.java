package com.enonic.xp.app;

/**
 * @deprecated Exists only for the {@link ApplicationInvalidator} signature, which is deprecated —
 * and the registry only ever passes {@link #FULL}. Scheduled for removal together with it.
 */
@Deprecated
public enum ApplicationInvalidationLevel
{
    CACHE, FULL
}
