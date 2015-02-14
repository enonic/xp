package com.enonic.xp.core.content;

import com.enonic.xp.core.index.ChildOrder;

public class SetContentChildOrderParams
{
    private final ContentId contentId;

    private final ChildOrder childOrder;

    private SetContentChildOrderParams( Builder builder )
    {
        contentId = builder.contentId;
        childOrder = builder.childOrder;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentId contentId;

        private ChildOrder childOrder;

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

        public SetContentChildOrderParams build()
        {
            return new SetContentChildOrderParams( this );
        }
    }
}
