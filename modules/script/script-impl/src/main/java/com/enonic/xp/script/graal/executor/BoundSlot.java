package com.enonic.xp.script.graal.executor;

/**
 * The slot bound to the current execution scope (nested executions must stay on it). Shared
 * across executors, so reads check slot ownership — a nested cross-app call must not adopt
 * the outer app's slot.
 * <p>
 * Kept apart from {@link GraalScriptExecutor}, whose initialization loads GraalJS: asking which
 * context a thread runs on must not load the engine where no application uses it.
 */
public final class BoundSlot
{
    static final ScopedValue<GraalScriptExecutor.ContextSlot> SLOT = ScopedValue.newInstance();

    private BoundSlot()
    {
    }

    static GraalScriptExecutor.ContextSlot get()
    {
        // not orElse(null): ScopedValue.orElse rejects null by spec
        return SLOT.isBound() ? SLOT.get() : null;
    }

    /**
     * Whether the calling thread runs script code on a context other than its application's main
     * context: a pooled, pinned or isolated one. Answers {@code false} on a thread that runs no
     * GraalJS script code.
     */
    public static boolean isOutsideMainContext()
    {
        final GraalScriptExecutor.ContextSlot bound = get();
        return bound != null && !bound.owner().isMainSlot( bound );
    }
}
