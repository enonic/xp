package com.enonic.xp.archive;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;

@PublicApi
public final class ArchiveContentsResult
{
    private final ContentIds archivedContents;

    private final ContentIds unpublishedContents;

    private ArchiveContentsResult( Builder builder )
    {
        this.archivedContents = builder.archivedContents.build();
        this.unpublishedContents = builder.unpublishedContents.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getArchivedContents()
    {
        return archivedContents;
    }

    public ContentIds getUnpublishedContents()
    {
        return unpublishedContents;
    }

    public static final class Builder
    {
        private final ContentIds.Builder archivedContents = ContentIds.create();

        private final ContentIds.Builder unpublishedContents = ContentIds.create();

        private Builder()
        {
        }

        public Builder addArchived( final ContentId contentId )
        {
            this.archivedContents.add( contentId );
            return this;
        }

        public Builder addArchived( final ContentIds archivedContents )
        {
            this.archivedContents.addAll( archivedContents );
            return this;
        }

        public Builder addUnpublished( final ContentIds unpublishedContents )
        {
            this.unpublishedContents.addAll( unpublishedContents );
            return this;
        }

        public ArchiveContentsResult build()
        {
            return new ArchiveContentsResult( this );
        }
    }
}
