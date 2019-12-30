package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;

@PublicApi
public class SetContentChildOrderParams
{
    private final ContentId contentId;

    private final ChildOrder childOrder;

    private final boolean silent;

    private SetContentChildOrderParams( Builder builder )
    {
        contentId = builder.contentId;
        childOrder = builder.childOrder;
        silent = builder.silent;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public boolean isSilent()
    {
        return silent;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentId contentId;

        private ChildOrder childOrder;

        private boolean silent;

        private Builder()
        {
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder silent( final boolean silent )
        {
            this.silent = silent;
            return this;
        }

        public SetContentChildOrderParams build()
        {
            return new SetContentChildOrderParams( this );
        }
    }
}
