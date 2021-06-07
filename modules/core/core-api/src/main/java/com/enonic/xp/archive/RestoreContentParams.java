package com.enonic.xp.archive;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;

@PublicApi
public final class RestoreContentParams
{
    private final ContentId contentId;

    private final ContentPath path;

    private final RestoreContentListener restoreContentListener;

    public RestoreContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.restoreContentListener = builder.restoreContentListener;
        this.path = builder.path;
    }

    public static RestoreContentParams.Builder create()
    {
        return new RestoreContentParams.Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentPath getPath()
    {
        return path;
    }

    public RestoreContentListener getRestoreContentListener()
    {
        return restoreContentListener;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ContentPath path;

        private RestoreContentListener restoreContentListener;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder path( final ContentPath path )
        {
            this.path = path;
            return this;
        }

        public Builder restoreContentListener( RestoreContentListener restoreContentListener )
        {
            this.restoreContentListener = restoreContentListener;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentId, "contentId must be set" );
        }

        public RestoreContentParams build()
        {
            validate();
            return new RestoreContentParams( this );
        }
    }
}
