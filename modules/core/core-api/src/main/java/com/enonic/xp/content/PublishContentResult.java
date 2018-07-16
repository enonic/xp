package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class PublishContentResult
{
    private final ContentIds pushedContents;

    private final ContentIds deletedContents;

    private final ContentIds failedContents;

    private final ContentPath deletedPath;

    private PublishContentResult( Builder builder )
    {
        this.pushedContents = builder.pushedContents;
        this.deletedContents = builder.deletedContents;
        this.failedContents = builder.failedContents;
        this.deletedPath = builder.deletedPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getPushedContents()
    {
        return pushedContents;
    }

    public ContentIds getDeletedContents()
    {
        return deletedContents;
    }

    public ContentIds getFailedContents()
    {
        return failedContents;
    }

    public ContentPath getDeletedPath()
    {
        return deletedPath;
    }

    public static final class Builder
    {
        private ContentIds pushedContents = ContentIds.empty();

        private ContentIds deletedContents = ContentIds.empty();

        private ContentIds failedContents = ContentIds.empty();

        private ContentPath deletedPath;

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

        public Builder setDeleted( final ContentIds deletedContents )
        {
            this.deletedContents = deletedContents;
            return this;
        }

        public Builder setDeletedPath( final ContentPath deletedPath )
        {
            this.deletedPath = deletedPath;
            return this;
        }

        public PublishContentResult build()
        {
            return new PublishContentResult( this );
        }
    }
}
