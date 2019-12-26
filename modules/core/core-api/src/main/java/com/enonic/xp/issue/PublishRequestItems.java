package com.enonic.xp.issue;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class PublishRequestItems
    extends AbstractImmutableEntitySet<PublishRequestItem>
    implements Iterable<PublishRequestItem>
{
    private PublishRequestItems( final ImmutableSet<PublishRequestItem> set )
    {
        super( set );
    }

    public static PublishRequestItems empty()
    {
        final ImmutableSet<PublishRequestItem> set = ImmutableSet.of();
        return new PublishRequestItems( set );
    }

    public static PublishRequestItems from( final PublishRequestItem... items )
    {
        return new PublishRequestItems( ImmutableSet.copyOf( items ) );
    }

    public static PublishRequestItems from( final Iterable<PublishRequestItem> items )
    {
        return new PublishRequestItems( ImmutableSet.copyOf( items ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<PublishRequestItem> items = new ArrayList<>();

        public Builder add( final PublishRequestItem item )
        {
            this.items.add( item );
            return this;
        }

        public Builder addAll( final PublishRequestItems items )
        {
            this.items.addAll( items.getSet() );
            return this;
        }


        public PublishRequestItems build()
        {
            return PublishRequestItems.from( items );
        }
    }
}
