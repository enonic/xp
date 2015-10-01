package com.enonic.xp.node;

import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Beta
public class NodeVersions
    implements Iterable<NodeVersion>
{
    private final NodeId nodeId;

    private final ImmutableList<NodeVersion> nodeVersions;

    private NodeVersions( Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.nodeVersions = ImmutableList.copyOf( builder.nodeVersions );
    }

    private NodeVersions( final NodeId nodeId, final ImmutableList<NodeVersion> nodeVersions )
    {
        this.nodeId = nodeId;
        this.nodeVersions = nodeVersions;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public static NodeVersions empty()
    {
        return new NodeVersions( null, ImmutableList.of() );
    }

    public static Builder create( final NodeId nodeId )
    {
        return new Builder( nodeId );
    }

    public NodeVersionIds getNodeVersionIds()
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();

        nodeVersions.forEach( ( nodeVersion ) -> builder.add( nodeVersion.getNodeVersionId() ) );

        return builder.build();
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
        private final List<NodeVersion> nodeVersions = Lists.newLinkedList();

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
