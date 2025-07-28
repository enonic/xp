package com.enonic.xp.issue;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class PublishRequestItems
    extends AbstractImmutableEntityList<PublishRequestItem>
{
    public static final PublishRequestItems EMPTY = new PublishRequestItems( ImmutableList.of() );

    private PublishRequestItems( final ImmutableList<PublishRequestItem> list )
    {
        super( list );
    }

    public static PublishRequestItems empty()
    {
        return EMPTY;
    }

    public static PublishRequestItems from( final PublishRequestItem... items )
    {
        return fromInternal( ImmutableList.copyOf( items ) );
    }

    public static PublishRequestItems from( final Iterable<PublishRequestItem> items )
    {
        return items instanceof PublishRequestItems p ? p : fromInternal( ImmutableList.copyOf( items ) );
    }

    public static Collector<PublishRequestItem, ?, PublishRequestItems> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), PublishRequestItems::fromInternal );
    }

    private static PublishRequestItems fromInternal( final ImmutableList<PublishRequestItem> items )
    {
        return items.isEmpty() ? EMPTY : new PublishRequestItems( items );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<PublishRequestItem> items = ImmutableList.builder();

        public Builder add( final PublishRequestItem item )
        {
            this.items.add( item );
            return this;
        }

        public Builder addAll( final Iterable<? extends PublishRequestItem> items )
        {
            this.items.addAll( items );
            return this;
        }

        public PublishRequestItems build()
        {
            return fromInternal( items.build() );
        }
    }
}
