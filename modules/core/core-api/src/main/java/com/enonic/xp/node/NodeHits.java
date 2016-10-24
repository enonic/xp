package com.enonic.xp.node;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntityList;

public class NodeHits
    extends AbstractImmutableEntityList<NodeHit>
{

    private NodeHits( final Collection<NodeHit> hits )
    {
        super( ImmutableList.copyOf( hits ) );
    }

    public static NodeHits empty()
    {
        final List<NodeHit> returnFields = Lists.newArrayList();
        return new NodeHits( returnFields );
    }

    private static NodeHits from( final Collection<NodeHit> returnFields )
    {
        return new NodeHits( returnFields );
    }

    public NodeIds getNodeIds()
    {
        return NodeIds.from( this.stream().map( NodeHit::getNodeId ).collect( Collectors.toList() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<NodeHit> nodeHits = Lists.newArrayList();

        public Builder add( final NodeHit nodeHit )
        {
            this.nodeHits.add( nodeHit );
            return this;
        }

        public NodeHits build()
        {
            return new NodeHits( this.nodeHits );
        }
    }

}
