package com.enonic.xp.context;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentConstants;

@PublicApi
public final class ContextAccessor
    extends ThreadLocal<Context>
{
    public final static ContextAccessor INSTANCE = new ContextAccessor();

    @Override
    protected Context initialValue()
    {
        final Context context = ContextBuilder.create().build();
        context.getLocalScope().setAttribute( ContentConstants.BRANCH_DRAFT );
        context.getLocalScope().setAttribute( ContentConstants.CONTENT_REPO.getId() );
        return context;
    }

    public static Context current()
    {
        return INSTANCE.get();
    }
}
