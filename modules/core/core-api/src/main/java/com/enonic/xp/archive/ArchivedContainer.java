package com.enonic.xp.archive;

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

    private ArchivedContainer( Builder builder )
    {
        this.id = builder.id;
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

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentId> contentIds = ImmutableList.builder();

        private ArchivedContainerId id;

        private Builder()
        {
        }

        public Builder id( final ArchivedContainerId id )
        {
            this.id = id;
            return this;
        }

        public Builder addContentIds( final Collection<ContentId> contentIds )
        {
            this.contentIds.addAll( contentIds );
            return this;
        }

        public ArchivedContainer build()
        {
            return new ArchivedContainer( this );
        }
    }
}
