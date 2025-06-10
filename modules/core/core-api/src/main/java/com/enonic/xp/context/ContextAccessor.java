package com.enonic.xp.context;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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
