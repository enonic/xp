package com.enonic.xp.issue;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;

public final class PublishRequest
{
    private final ContentIds excludeIds;

    private final PublishRequestItems items;

    private PublishRequest( Builder builder )
    {
        this.items = builder.items.build();
        this.excludeIds = builder.exclude.build();
    }

    public PublishRequestItems getItems()
    {
        return items;
    }

    public ContentIds getExcludeIds()
    {
        return excludeIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final PublishRequestItems.Builder items = PublishRequestItems.create();

        private final ContentIds.Builder exclude = ContentIds.create();

        private Builder()
        {
        }

        public Builder addItems( final PublishRequestItems items )
        {
            this.items.addAll( items );
            return this;
        }

        public Builder addItem( final PublishRequestItem item )
        {
            this.items.add( item );
            return this;
        }

        public Builder addExcludeIds( final ContentIds exclude )
        {
            this.exclude.addAll( exclude );
            return this;
        }

        public Builder addExcludeId( final ContentId exclude )
        {
            this.exclude.add( exclude );
            return this;
        }

        public PublishRequest build()
        {
            return new PublishRequest( this );
        }
    }
}
