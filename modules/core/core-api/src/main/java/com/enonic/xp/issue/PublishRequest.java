package com.enonic.xp.issue;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;

public final class PublishRequest
{
    private final ContentIds excludeIds;

    private final PublishRequestItems items;

    private PublishRequest( Builder builder )
    {
        this.items = PublishRequestItems.from( builder.items );
        this.excludeIds = ContentIds.from( builder.exclude );
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

    public static class Builder
    {
        private Set<PublishRequestItem> items = Sets.newLinkedHashSet();

        private Set<ContentId> exclude = Sets.newLinkedHashSet();

        public Builder addItems( final Collection<PublishRequestItem> items )
        {
            this.items.addAll( items );
            return this;
        }

        public Builder addItems( final PublishRequestItems items )
        {
            this.items.addAll( items.getSet() );
            return this;
        }

        public Builder addItem( final PublishRequestItem item )
        {
            this.items.add( item );
            return this;
        }


        public Builder addExcludeIds( final Collection<ContentId> exclude )
        {
            this.exclude.addAll( exclude );
            return this;
        }

        public Builder addExcludeIds( final ContentIds exclude )
        {
            this.exclude.addAll( exclude.getSet() );
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
