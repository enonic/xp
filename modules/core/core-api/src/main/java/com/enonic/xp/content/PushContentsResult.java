package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class PushContentsResult
{
    private final ContentIds pushedContent;

    private final ContentIds deletedContent;

    private final ContentIds failedContent;

    private PushContentsResult( Builder builder )
    {
        this.pushedContent = builder.pushedContent;
        this.deletedContent = builder.deletedContent;
        this.failedContent = builder.failedContent;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getPushedContent()
    {
        return pushedContent;
    }

    public ContentIds getDeletedContent()
    {
        return deletedContent;
    }

    public ContentIds getFailedContent()
    {
        return failedContent;
    }

    public static final class Builder
    {
        private ContentIds pushedContent = ContentIds.empty();

        private ContentIds deletedContent = ContentIds.empty();

        private ContentIds failedContent = ContentIds.empty();

        private Builder()
        {
        }

        public Builder setPushed( final ContentIds pushedContent )
        {
            this.pushedContent = pushedContent;
            return this;
        }

        public Builder setFailed( final ContentIds failedContent )
        {
            this.failedContent = failedContent;
            return this;
        }

        public Builder setDeleted( final ContentIds deletedContent )
        {
            this.pushedContent = deletedContent;
            return this;
        }


        public PushContentsResult build()
        {
            return new PushContentsResult( this );
        }
    }
}