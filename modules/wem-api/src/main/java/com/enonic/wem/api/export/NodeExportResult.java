package com.enonic.wem.api.export;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;

public class NodeExportResult
{
    private final NodeIds exportedNodes;

    private NodeExportResult( final Builder builder )
    {
        exportedNodes = NodeIds.from( builder.nodeIds );
    }

    public NodeIds getExportedNodes()
    {
        return exportedNodes;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public int size()
    {
        return exportedNodes.getSize();
    }


    public static final class Builder
    {
        private final Set<NodeId> nodeIds = Sets.newLinkedHashSet();

        private Builder()
        {
        }

        public Builder add( NodeId nodeId )
        {
            this.nodeIds.add( nodeId );
            return this;
        }

        public NodeExportResult build()
        {
            return new NodeExportResult( this );
        }
    }
}
