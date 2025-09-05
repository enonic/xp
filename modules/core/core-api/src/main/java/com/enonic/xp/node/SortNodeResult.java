package com.enonic.xp.node;

import com.google.common.collect.ImmutableMap;

public final class SortNodeResult
{
    private final Node node;

    private final Nodes reorderedNodes;

    private SortNodeResult( final Builder builder )
    {
        this.node = builder.node;
        this.reorderedNodes = Nodes.from( builder.reorderedNodes.buildKeepingLast().values() );
    }

    public Node getNode()
    {
        return node;
    }

    public Nodes getReorderedNodes()
    {
        return reorderedNodes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Node node;

        private ImmutableMap.Builder<NodeId, Node> reorderedNodes = ImmutableMap.builder();

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder addReorderedNode( Node node )
        {
            reorderedNodes.put( node.id(), node );
            return this;
        }

        public SortNodeResult build()
        {
            return new SortNodeResult( this );
        }

        private Builder()
        {
        }
    }
}
