package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;

@PublicApi
public final class SetContentChildOrderParams
{
    private final ContentId contentId;

    private final ChildOrder childOrder;

    private final boolean stopInherit;

    private SetContentChildOrderParams( Builder builder )
    {
        contentId = builder.contentId;
        childOrder = builder.childOrder;
        stopInherit = builder.stopInherit;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public boolean stopInherit()
    {
        return stopInherit;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentId contentId;

        private ChildOrder childOrder;

        private boolean stopInherit = true;

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

        public Builder stopInherit( final boolean stopInherit )
        {
            this.stopInherit = stopInherit;
            return this;
        }

        public SetContentChildOrderParams build()
        {
            return new SetContentChildOrderParams( this );
        }
    }
}
