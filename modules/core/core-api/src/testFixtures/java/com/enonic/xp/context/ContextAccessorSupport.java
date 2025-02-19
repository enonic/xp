package com.enonic.xp.context;

public final class ContextAccessorSupport
{
    private ContextAccessorSupport()
    {
    }

    public static ThreadLocal<Context> getInstance()
    {
        return ContextAccessor.INSTANCE;
    }
}
