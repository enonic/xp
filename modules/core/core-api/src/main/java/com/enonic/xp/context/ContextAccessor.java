package com.enonic.xp.context;

public final class ContextAccessor
{
    static final ScopedValue<Context> INSTANCE = ScopedValue.newInstance();

    public static Context current()
    {
        return INSTANCE.isBound() ? INSTANCE.get() : ContextBuilder.create().build();
    }
}
