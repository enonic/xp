package com.enonic.wem.api.context;

import com.enonic.wem.api.content.ContentConstants;

public final class ContextAccessor
    extends ThreadLocal<Context>
{
    public final static ContextAccessor INSTANCE = new ContextAccessor();

    @Override
    protected Context initialValue()
    {
        final Context context = ContextBuilder.create().build();
        context.getLocalScope().setAttribute( ContentConstants.WORKSPACE_DRAFT );
        context.getLocalScope().setAttribute( ContentConstants.CONTENT_REPO.getId() );
        return context;
    }

    public static Context current()
    {
        return INSTANCE.get();
    }
}
