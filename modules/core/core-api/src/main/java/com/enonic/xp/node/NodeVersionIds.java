package com.enonic.xp.node;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class NodeVersionIds
    extends AbstractImmutableEntitySet<NodeVersionId>
{
    private static final NodeVersionIds EMPTY = new NodeVersionIds( ImmutableSet.of() );

    private NodeVersionIds( final ImmutableSet<NodeVersionId> set )
    {
        super( set );
    }

    public static NodeVersionIds empty()
    {
        return EMPTY;
    }

    public static NodeVersionIds from( final NodeVersionId... nodeVersionIds )
    {
        return fromInternal( ImmutableSet.copyOf( nodeVersionIds ) );
    }

    public static NodeVersionIds from( final Iterable<? extends NodeVersionId> nodeVersionIds )
    {
        return fromInternal( ImmutableSet.copyOf( nodeVersionIds ) );
    }

    public static Collector<NodeVersionId, ?, NodeVersionIds> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), NodeVersionIds::fromInternal );
    }

    private static NodeVersionIds fromInternal( final ImmutableSet<NodeVersionId> nodeVersionIds )
    {
        return nodeVersionIds.isEmpty() ? EMPTY : new NodeVersionIds( nodeVersionIds );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<NodeVersionId> nodeVersionIds = ImmutableSet.builder();

        public Builder add( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionIds.add( nodeVersionId );
            return this;
        }

        public Builder addAll( final Iterable<? extends NodeVersionId> nodeVersionIds )
        {
            this.nodeVersionIds.addAll( nodeVersionIds );
            return this;
        }

        public NodeVersionIds build()
        {
            return new NodeVersionIds( this.nodeVersionIds.build() );
        }
    }
}
