package com.enonic.xp.context;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentConstants;

@PublicApi
public final class ContextAccessor
{
    static final ThreadLocal<Context> INSTANCE = ThreadLocal.withInitial( ContextAccessor::initialValue );

    private static Context initialValue()
    {
        final Context context = ContextBuilder.create().build();
        context.getLocalScope().setAttribute( ContentConstants.BRANCH_DRAFT );
        return context;
    }

    public static Context current()
    {
        return INSTANCE.get();
    }
}
