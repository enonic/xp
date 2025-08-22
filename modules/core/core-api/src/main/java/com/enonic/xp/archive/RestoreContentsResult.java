package com.enonic.xp.archive;

import java.util.Objects;

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
        this.restoredContents = builder.restoredContents.build();
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
        private final ContentIds.Builder restoredContents = ContentIds.create();

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
            Objects.requireNonNull( parentPath );
        }

        public RestoreContentsResult build()
        {
            validate();
            return new RestoreContentsResult( this );
        }
    }
}
