package com.enonic.xp.issue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;

public final class PublishRequest
{
    private final ContentIds excludeIds;

    private final PublishRequestItems items;

    private PublishRequest( Builder builder )
    {
        this.items = PublishRequestItems.from( builder.items );
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

    public static class Builder
    {
        private final List<PublishRequestItem> items = new ArrayList<>();

        private final ContentIds.Builder exclude = ContentIds.create();

        public Builder addItems( final Collection<PublishRequestItem> items )
        {
            this.items.addAll( items );
            return this;
        }

        public Builder addItems( final PublishRequestItems items )
        {
            this.items.addAll( items.getList() );
            return this;
        }

        public Builder addItem( final PublishRequestItem item )
        {
            this.items.add( item );
            return this;
        }


        public Builder addExcludeIds( final Collection<ContentId> exclude )
        {
            exclude.forEach( this.exclude::add );
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
