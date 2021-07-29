package com.enonic.xp.archive;

import java.time.Instant;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;

@PublicApi
public class ArchivedContainer
{
    private final ContentIds contentIds;

    private final ArchivedContainerId id;

    private final Instant archiveTime;

    private final ContentId parent;

    private ArchivedContainer( Builder builder )
    {
        this.id = builder.id;
        this.archiveTime = builder.archiveTime;
        this.parent = builder.parent;
        this.contentIds = ContentIds.from( builder.contentIds.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public ArchivedContainerId getId()
    {
        return id;
    }

    public Instant getArchiveTime()
    {
        return archiveTime;
    }

    public ContentId getParent()
    {
        return parent;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentId> contentIds = ImmutableList.builder();

        private ArchivedContainerId id;

        private Instant archiveTime;

        private ContentId parent;

        private Builder()
        {
        }

        public Builder id( final ArchivedContainerId id )
        {
            this.id = id;
            return this;
        }

        public Builder archiveTime( final Instant archiveTime )
        {
            this.archiveTime = archiveTime;
            return this;
        }

        public Builder addContentIds( final Collection<ContentId> contentIds )
        {
            this.contentIds.addAll( contentIds );
            return this;
        }

        public Builder parent( final ContentId parent )
        {
            this.parent = parent;
            return this;
        }

        public ArchivedContainer build()
        {
            return new ArchivedContainer( this );
        }
    }
}
