package com.enonic.xp.node;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class NodeHits
    extends AbstractImmutableEntityList<NodeHit>
{

    private NodeHits( final ImmutableList<NodeHit> hits )
    {
        super( hits );
    }

    public static NodeHits empty()
    {
        return new NodeHits( ImmutableList.of() );
    }

    public NodeIds getNodeIds()
    {
        return NodeIds.from( this.stream().map( NodeHit::getNodeId ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableList.Builder<NodeHit> nodeHits = ImmutableList.builder();

        public Builder add( final NodeHit nodeHit )
        {
            this.nodeHits.add( nodeHit );
            return this;
        }

        public NodeHits build()
        {
            return new NodeHits( this.nodeHits.build() );
        }
    }
}
