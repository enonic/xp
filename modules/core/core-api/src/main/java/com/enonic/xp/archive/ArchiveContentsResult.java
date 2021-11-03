package com.enonic.xp.archive;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;

@PublicApi
public final class ArchiveContentsResult
{
    private final ContentIds archivedContents;

    private ArchiveContentsResult( Builder builder )
    {
        this.archivedContents = ContentIds.from( builder.archivedContents.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getArchivedContents()
    {
        return archivedContents;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentId> archivedContents = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder addArchived( final ContentId contentId )
        {
            this.archivedContents.add( contentId );
            return this;
        }

        public ArchiveContentsResult build()
        {
            return new ArchiveContentsResult( this );
        }
    }
}
