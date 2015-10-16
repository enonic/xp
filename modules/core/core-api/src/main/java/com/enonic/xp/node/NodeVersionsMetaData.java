package com.enonic.xp.node;

import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Beta
public class NodeVersionsMetaData
    implements Iterable<NodeVersionMetadata>
{
    private final NodeId nodeId;

    private final ImmutableList<NodeVersionMetadata> nodeVersionMetadatas;

    private NodeVersionsMetaData( Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.nodeVersionMetadatas = ImmutableList.copyOf( builder.nodeVersionMetadatas );
    }

    private NodeVersionsMetaData( final NodeId nodeId, final ImmutableList<NodeVersionMetadata> nodeVersionMetadatas )
    {
        this.nodeId = nodeId;
        this.nodeVersionMetadatas = nodeVersionMetadatas;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public static NodeVersionsMetaData empty()
    {
        return new NodeVersionsMetaData( null, ImmutableList.of() );
    }

    public static Builder create( final NodeId nodeId )
    {
        return new Builder( nodeId );
    }

    public NodeVersionIds getNodeVersionIds()
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();

        nodeVersionMetadatas.forEach( ( nodeVersion ) -> builder.add( nodeVersion.getNodeVersionId() ) );

        return builder.build();
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
        private final List<NodeVersionMetadata> nodeVersionMetadatas = Lists.newLinkedList();

        private final NodeId nodeId;

        private Builder( final NodeId nodeId )
        {
            this.nodeId = nodeId;
        }

        public Builder add( final NodeVersionMetadata nodeVersionMetadata )
        {
            this.nodeVersionMetadatas.add( nodeVersionMetadata );
            return this;
        }

        public NodeVersionsMetaData build()
        {
            return new NodeVersionsMetaData( this );
        }
    }
}
