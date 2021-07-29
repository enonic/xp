package com.enonic.xp.archive;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;

@PublicApi
public final class ListContentsParams
{
    private final ContentId parent;

    public ListContentsParams( Builder builder )
    {
        this.parent = builder.parent;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getParent()
    {
        return parent;
    }

    public void validate()
    {
    }

    public static final class Builder
    {
        private ContentId parent;

        private Builder()
        {
        }

        public Builder parent( ContentId parent )
        {
            this.parent = parent;
            return this;
        }

        private void validate()
        {
        }

        public ListContentsParams build()
        {
            validate();
            return new ListContentsParams( this );
        }
    }
}
