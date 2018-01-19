package com.enonic.xp.cluster;

import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public class ClusterNodes
    extends AbstractImmutableEntitySet<ClusterNode>
{
    public ClusterNodes( final ImmutableSet<ClusterNode> set )
    {
        super( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<ClusterNode> nodes = Lists.newArrayList();

        public Builder add( final ClusterNode node )
        {
            this.nodes.add( node );
            return this;
        }

        public ClusterNodes build()
        {
            return new ClusterNodes( ImmutableSet.copyOf( nodes ) );
        }
    }

}
