package com.enonic.xp.core.impl.app;

public @interface SchemaConfig
{
    long DEFAULT_CACHE_CHECK_INTERVAL = 1000;

    boolean auditlog_enabled() default true;

    /**
     * How long, in milliseconds, the persisted schema of an application is trusted without reading the version of its node again.
     * Bounds how stale the schema cached on a cluster node can get when the event telling about a change does not arrive.
     * {@code 0} checks on every read.
     */
    long cache_checkInterval() default DEFAULT_CACHE_CHECK_INTERVAL;
}
