package com.enonic.xp.archive;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;

@PublicApi
public final class RestoreContentsResult
{
    private final ContentIds restoredContents;

    private final ContentPath parentPath;

    private RestoreContentsResult( Builder builder )
    {
        this.restoredContents = ContentIds.from( builder.restoredContents.build() );
        this.parentPath = builder.parentPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getRestoredContents()
    {
        return restoredContents;
    }

    public ContentPath getParentPath()
    {
        return parentPath;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentId> restoredContents = ImmutableList.builder();

        private ContentPath parentPath;

        private Builder()
        {
        }

        public Builder addRestored( final ContentId contentId )
        {
            this.restoredContents.add( contentId );
            return this;
        }

        public Builder parentPath( final ContentPath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( parentPath, "parentPath must be set" );
        }

        public RestoreContentsResult build()
        {
            validate();
            return new RestoreContentsResult( this );
        }
    }
}
