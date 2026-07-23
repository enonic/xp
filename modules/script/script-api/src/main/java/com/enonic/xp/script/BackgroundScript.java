package com.enonic.xp.script;

/**
 * A script resolved for background execution. Deliberately minimal: on pooled script engines
 * every invocation runs in a fresh private context that closes when the invocation returns, so
 * a result could never outlive the call — invocations return nothing by design, and background
 * functions communicate through platform APIs instead.
 */
@FunctionalInterface
public interface BackgroundScript
{
    void executeMethod( String name, Object... args );
}
