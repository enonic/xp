package com.enonic.xp.node;

public class ReorderChildNodesResult
{
    private final NodeIds nodeIds;

    private ReorderChildNodesResult( Builder builder )
    {
        nodeIds = builder.nodeIds.build();
    }

    public int getSize()
    {
        return nodeIds.getSize();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        final NodeIds.Builder nodeIds = NodeIds.create();

        private Builder()
        {
        }

        public Builder addNodeId( final NodeId nodeIds )
        {
            this.nodeIds.add( nodeIds );
            return this;
        }

        public ReorderChildNodesResult build()
        {
            return new ReorderChildNodesResult( this );
        }
    }
}
