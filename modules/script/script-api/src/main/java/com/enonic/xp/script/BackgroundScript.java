package com.enonic.xp.script;

/**
 * One script method resolved for background execution: the script and the method are bound at
 * resolve time, and every {@link #execute} runs them on a pooled engine in a fresh private
 * context that closes when the invocation returns. Nothing is shared between invocations, and
 * a result could never outlive the call — invocations return nothing by design, and background
 * functions communicate through platform APIs instead.
 */
@FunctionalInterface
public interface BackgroundScript
{
    void execute( Object... args );
}
