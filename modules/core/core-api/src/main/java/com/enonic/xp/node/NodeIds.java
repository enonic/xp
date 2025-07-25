package com.enonic.xp.node;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class NodeIds
    extends AbstractImmutableEntitySet<NodeId>
{
    private static final NodeIds EMPTY = new NodeIds( ImmutableSet.of() );

    private NodeIds( final ImmutableSet<NodeId> set )
    {
        super( set );
    }

    public static NodeIds empty()
    {
        return EMPTY;
    }

    public static NodeIds from( final NodeId... ids )
    {
        return fromInternal( ImmutableSet.copyOf( ids ) );
    }

    public static NodeIds from( final String... ids )
    {
        return from( Arrays.asList( ids ) );
    }

    public static NodeIds from( final Collection<String> ids )
    {
        return ids.stream().map( NodeId::from ).collect( collector() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static NodeIds from( final Iterable<NodeId> ids )
    {
        return ids instanceof NodeIds ? (NodeIds) ids : fromInternal( ImmutableSet.copyOf( ids ) );
    }

    public static Collector<NodeId, ?, NodeIds> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), NodeIds::fromInternal );
    }

    private static NodeIds fromInternal( final ImmutableSet<NodeId> set )
    {
        return set.isEmpty() ? EMPTY : new NodeIds( set );
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<NodeId> nodeIds = ImmutableSet.builder();

        public Builder add( final NodeId nodeId )
        {
            this.nodeIds.add( nodeId );
            return this;
        }

        public Builder addAll( final Iterable<? extends NodeId> nodeIds )
        {
            this.nodeIds.addAll( nodeIds );
            return this;
        }

        public NodeIds build()
        {
            return fromInternal( nodeIds.build() );
        }
    }
}
