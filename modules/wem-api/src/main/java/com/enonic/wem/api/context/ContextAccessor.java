package com.enonic.wem.api.context;

import com.enonic.wem.api.content.ContentConstants;

public final class ContextAccessor
    extends ThreadLocal<Context>
{
    public final static ContextAccessor INSTANCE = new ContextAccessor();

    private ContextAccessor()
    {
    }

    @Override
    protected Context initialValue()
    {
        return ContentConstants.CONTEXT_STAGE;
    }

    public static Context current()
    {
        if ( INSTANCE.get() == null )
        {
            throw new IllegalStateException( "No context set" );
        }

        return INSTANCE.get();
    }
}
