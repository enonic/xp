package com.enonic.xp.node;

import java.util.Iterator;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

public class NodeVersions
    implements Iterable<NodeVersion>
{
    private final NodeId nodeId;

    private final ImmutableSortedSet<NodeVersion> nodeVersions;

    private NodeVersions( Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.nodeVersions = ImmutableSortedSet.copyOf( builder.nodeVersions );
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public static Builder create( final NodeId nodeId )
    {
        return new Builder( nodeId );
    }

    @Override
    public Iterator<NodeVersion> iterator()
    {
        return nodeVersions.iterator();
    }

    public int size()
    {
        return nodeVersions.size();
    }

    public static final class Builder
    {
        private final SortedSet<NodeVersion> nodeVersions = Sets.newTreeSet();

        private final NodeId nodeId;

        private Builder( final NodeId nodeId )
        {
            this.nodeId = nodeId;
        }

        public Builder add( final NodeVersion nodeVersion )
        {
            this.nodeVersions.add( nodeVersion );
            return this;
        }

        public NodeVersions build()
        {
            return new NodeVersions( this );
        }
    }
}
