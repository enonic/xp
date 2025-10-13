package com.enonic.xp.node;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Deprecated
public final class RoutableNodeVersionIds
    extends AbstractImmutableEntityList<RoutableNodeVersionId>
{
    private static final RoutableNodeVersionIds EMPTY = new RoutableNodeVersionIds( ImmutableList.of() );

    private RoutableNodeVersionIds( final ImmutableList<RoutableNodeVersionId> list )
    {
        super( list );
    }

    public static RoutableNodeVersionIds empty()
    {
        return new RoutableNodeVersionIds( ImmutableList.of() );
    }

    public static RoutableNodeVersionIds from( final RoutableNodeVersionId... routableNodeVersionIds )
    {
        return fromInternal( ImmutableList.copyOf( routableNodeVersionIds ) );
    }

    public static RoutableNodeVersionIds from( final Iterable<RoutableNodeVersionId> routableNodeVersionIds )
    {
        return routableNodeVersionIds instanceof RoutableNodeVersionIds r ? r : fromInternal( ImmutableList.copyOf( routableNodeVersionIds ) );
    }

    public static Collector<RoutableNodeVersionId, ?, RoutableNodeVersionIds> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), RoutableNodeVersionIds::fromInternal );
    }

    private static RoutableNodeVersionIds fromInternal( final ImmutableList<RoutableNodeVersionId> set )
    {
        return set.isEmpty() ? EMPTY : new RoutableNodeVersionIds( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        final ImmutableList.Builder<RoutableNodeVersionId> routableNodeVersionIds = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( final RoutableNodeVersionId routableNodeVersionId )
        {
            this.routableNodeVersionIds.add( routableNodeVersionId );
            return this;
        }

        public Builder addAll( final Iterable<RoutableNodeVersionId> routableNodeVersionIds )
        {
            this.routableNodeVersionIds.addAll( routableNodeVersionIds );
            return this;
        }

        public RoutableNodeVersionIds build()
        {
            return new RoutableNodeVersionIds( routableNodeVersionIds.build() );
        }
    }
}
