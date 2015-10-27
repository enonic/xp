package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class PushContentsResult
{
    private final Contents pushedContents;

    private final Contents deletedContents;

    private final Contents failedContents;

    private PushContentsResult( Builder builder )
    {
        this.pushedContents = builder.pushedContents;
        this.deletedContents = builder.deletedContents;
        this.failedContents = builder.failedContents;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Contents getPushedContents()
    {
        return pushedContents;
    }

    public Contents getDeletedContents()
    {
        return deletedContents;
    }

    public Contents getFailedContents()
    {
        return failedContents;
    }

    public static final class Builder
    {
        private Contents pushedContents = Contents.empty();

        private Contents deletedContents = Contents.empty();

        private Contents failedContents = Contents.empty();

        private Builder()
        {
        }

        public Builder setPushed( final Contents pushedContents )
        {
            this.pushedContents = pushedContents;
            return this;
        }

        public Builder setFailed( final Contents failedContents )
        {
            this.failedContents = failedContents;
            return this;
        }

        public Builder setDeleted( final Contents deletedContents )
        {
            this.deletedContents = deletedContents;
            return this;
        }


        public PushContentsResult build()
        {
            return new PushContentsResult( this );
        }
    }
}