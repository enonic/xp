package com.enonic.xp.cluster;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public final class  ClusterNodes
    extends AbstractImmutableEntitySet<ClusterNode>
{
    private ClusterNodes( final ImmutableSet<ClusterNode> set )
    {
        super( set );
    }

    public static Collector<ClusterNode, ?, ClusterNodes> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), ClusterNodes::new );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<ClusterNode> nodes = ImmutableSet.builder();

        private Builder()
        {
        }

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
