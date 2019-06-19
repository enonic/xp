package com.enonic.xp.node;

public class DuplicateNodeResult
{
    private Node duplicatedNode;

    private NodeIds duplicatedNodeIds;

    private DuplicateNodeResult( final Builder builder )
    {
        duplicatedNode = builder.duplicatedNode;
        duplicatedNodeIds = builder.duplicatedNodeIds.build();
    }

    public Node getDuplicatedNode()
    {
        return duplicatedNode;
    }

    public NodeIds getDuplicatedNodeIds()
    {
        return duplicatedNodeIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Node duplicatedNode;

        private NodeIds.Builder duplicatedNodeIds = NodeIds.create();

        private Builder()
        {
        }

        public Builder duplicatedNode( final Node duplicatedNode )
        {
            this.duplicatedNode = duplicatedNode;
            return this;
        }

        public Builder addDuplicatedNodeId( final NodeId duplicatedNodeId )
        {
            this.duplicatedNodeIds.add( duplicatedNodeId);
            return this;
        }

        public DuplicateNodeResult build()
        {
            return new DuplicateNodeResult( this );
        }
    }
}
