package com.enonic.xp.archive;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;

@PublicApi
public final class RestoreContentParams
{
    private final ContentId contentId;

    private final ContentPath parentPath;

    private final RestoreContentListener restoreContentListener;

    private final boolean stopInherit;

    private RestoreContentParams( final Builder builder )
    {
        this.contentId = builder.contentId;
        this.parentPath = builder.parentPath;
        this.restoreContentListener = builder.restoreContentListener;
        this.stopInherit = builder.stopInherit;

    }

    public static RestoreContentParams.Builder create()
    {
        return new RestoreContentParams.Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentPath getParentPath()
    {
        return parentPath;
    }

    public RestoreContentListener getRestoreContentListener()
    {
        return restoreContentListener;
    }

    public boolean stopInherit()
    {
        return stopInherit;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ContentPath parentPath;

        private RestoreContentListener restoreContentListener;

        private boolean stopInherit = true;

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
            this.parentPath = path;
            return this;
        }

        public Builder restoreContentListener( RestoreContentListener restoreContentListener )
        {
            this.restoreContentListener = restoreContentListener;
            return this;
        }

        public Builder stopInherit( boolean stopInherit )
        {
            this.stopInherit = stopInherit;
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
