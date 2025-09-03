package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class SortContentResult
{
    private final ContentIds movedChildren;

    private final Content content;

    private SortContentResult( final Builder builder )
    {
        this.content = builder.content;
        this.movedChildren = builder.movedChildren;
    }

    public ContentIds getMovedChildren()
    {
        return movedChildren;
    }

    public Content getContent()
    {
        return content;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Content content;

        private ContentIds movedChildren;

        private Builder()
        {
        }

        public Builder content( final Content content )
        {
            this.content = content;
            return this;
        }

        public Builder movedChildren( final ContentIds movedChildren )
        {
            this.movedChildren = movedChildren;
            return this;
        }

        public SortContentResult build()
        {
            return new SortContentResult( this );
        }
    }
}
