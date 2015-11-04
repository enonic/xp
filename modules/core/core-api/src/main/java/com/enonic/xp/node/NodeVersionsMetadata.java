package com.enonic.xp.node;

import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Beta
public class NodeVersionsMetadata
    implements Iterable<NodeVersionMetadata>
{
    private final NodeId nodeId;

    private final ImmutableList<NodeVersionMetadata> nodeVersionMetadatas;

    private NodeVersionsMetadata( Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.nodeVersionMetadatas = ImmutableList.copyOf( builder.nodeVersionMetadata );
    }

    private NodeVersionsMetadata( final NodeId nodeId, final ImmutableList<NodeVersionMetadata> nodeVersionMetadatas )
    {
        this.nodeId = nodeId;
        this.nodeVersionMetadatas = nodeVersionMetadatas;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public static NodeVersionsMetadata empty()
    {
        return new NodeVersionsMetadata( null, ImmutableList.of() );
    }

    public static Builder create( final NodeId nodeId )
    {
        return new Builder( nodeId );
    }

    @Override
    public Iterator<NodeVersionMetadata> iterator()
    {
        return nodeVersionMetadatas.iterator();
    }

    public int size()
    {
        return nodeVersionMetadatas.size();
    }

    public static final class Builder
    {
        private final List<NodeVersionMetadata> nodeVersionMetadata = Lists.newLinkedList();

        private final NodeId nodeId;

        private Builder( final NodeId nodeId )
        {
            this.nodeId = nodeId;
        }

        public Builder add( final NodeVersionMetadata nodeVersionMetadata )
        {
            this.nodeVersionMetadata.add( nodeVersionMetadata );
            return this;
        }

        public NodeVersionsMetadata build()
        {
            return new NodeVersionsMetadata( this );
        }
    }
}
