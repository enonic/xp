package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ReorderChildNodesResult
{
    private final NodeIds nodeIds;

    private final Nodes parentNodes;

    private ReorderChildNodesResult( Builder builder )
    {
        nodeIds = builder.nodeIds.build();
        parentNodes = builder.parentNodes.build();
    }

    public int getSize()
    {
        return nodeIds.getSize();
    }

    public Nodes getParentNodes()
    {
        return parentNodes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        final NodeIds.Builder nodeIds = NodeIds.create();

        final Nodes.Builder parentNodes = Nodes.create();

        private Builder()
        {
        }

        public Builder addNodeId( final NodeId nodeIds )
        {
            this.nodeIds.add( nodeIds );
            return this;
        }

        public Builder addParentNode( final Node parentNode )
        {
            this.parentNodes.add( parentNode );
            return this;
        }

        public ReorderChildNodesResult build()
        {
            return new ReorderChildNodesResult( this );
        }
    }
}
