package com.enonic.xp.context;

public final class ContextAccessor
{
    static final ThreadLocal<Context> INSTANCE = ThreadLocal.withInitial( ContextAccessor::initialValue );

    private static Context initialValue()
    {
        return ContextBuilder.create().build();
    }

    public static Context current()
    {
        return INSTANCE.get();
    }
}
