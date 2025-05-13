package com.enonic.xp.cluster;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public final class ClusterNodes
    extends AbstractImmutableEntitySet<ClusterNode>
{
    private ClusterNodes( final ImmutableSet<ClusterNode> set, boolean ignore )
    {
        super( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<ClusterNode> nodes = new ArrayList<>();

        public Builder add( final ClusterNode node )
        {
            this.nodes.add( node );
            return this;
        }

        public ClusterNodes build()
        {
            return new ClusterNodes( ImmutableSet.copyOf( nodes ), false );
        }
    }

}
