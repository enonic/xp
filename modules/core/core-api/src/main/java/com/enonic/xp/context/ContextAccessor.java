package com.enonic.xp.context;

public final class ContextAccessor
{
    static final ScopedValue<Context> INSTANCE = ScopedValue.newInstance();

    // Backs the imperative test support in ContextAccessorSupport. Production code must bind through runWith/callWith.
    static final ThreadLocal<Context> LEGACY = new ThreadLocal<>();

    public static Context current()
    {
        if ( INSTANCE.isBound() )
        {
            return INSTANCE.get();
        }
        final Context legacy = LEGACY.get();
        return legacy != null ? legacy : ContextBuilder.create().build();
    }
}
