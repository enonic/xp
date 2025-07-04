package com.enonic.xp.node;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class NodeHits
    extends AbstractImmutableEntityList<NodeHit>
{
    private static final NodeHits EMPTY = new NodeHits( ImmutableList.of() );

    private NodeHits( final ImmutableList<NodeHit> hits )
    {
        super( hits );
    }

    public static NodeHits empty()
    {
        return EMPTY;
    }

    public NodeIds getNodeIds()
    {
        return NodeIds.from( this.stream().map( NodeHit::getNodeId ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public static Collector<NodeHit, ?, NodeHits> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), NodeHits::fromInternal );
    }

    private static NodeHits fromInternal( final ImmutableList<NodeHit> hits )
    {
        return hits.isEmpty() ? EMPTY : new NodeHits( hits );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<NodeHit> nodeHits = ImmutableList.builder();

        public Builder add( final NodeHit nodeHit )
        {
            this.nodeHits.add( nodeHit );
            return this;
        }

        public NodeHits build()
        {
            return fromInternal( this.nodeHits.build() );
        }
    }
}
