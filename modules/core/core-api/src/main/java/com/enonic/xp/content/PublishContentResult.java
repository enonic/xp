package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PublishContentResult
{
    private final ContentIds pushedContents;

    private final ContentIds failedContents;

    private PublishContentResult( Builder builder )
    {
        this.pushedContents = builder.pushedContents;
        this.failedContents = builder.failedContents;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getPushedContents()
    {
        return pushedContents;
    }

    public ContentIds getFailedContents()
    {
        return failedContents;
    }

    public static final class Builder
    {
        private ContentIds pushedContents = ContentIds.empty();

        private ContentIds failedContents = ContentIds.empty();

        private Builder()
        {
        }

        public Builder setPushed( final ContentIds pushedContents )
        {
            this.pushedContents = pushedContents;
            return this;
        }

        public Builder setFailed( final ContentIds failedContents )
        {
            this.failedContents = failedContents;
            return this;
        }

        public PublishContentResult build()
        {
            return new PublishContentResult( this );
        }
    }
}
