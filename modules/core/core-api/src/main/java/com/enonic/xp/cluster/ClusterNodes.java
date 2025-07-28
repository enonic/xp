package com.enonic.xp.cluster;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class ClusterNodes
    extends AbstractImmutableEntityList<ClusterNode>
{
    private ClusterNodes( final ImmutableList<ClusterNode> set )
    {
        super( set );
    }

    public static Collector<ClusterNode, ?, ClusterNodes> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ClusterNodes::new );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ClusterNode> nodes = ImmutableList.builder();

        public Builder add( final ClusterNode node )
        {
            this.nodes.add( node );
            return this;
        }

        public ClusterNodes build()
        {
            return new ClusterNodes( nodes.build() );
        }
    }
}
