package com.enonic.xp.node;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class SortNodeResult
{
    private final Node node;

    private final ImmutableList<Node> reorderedNodes;

    private SortNodeResult( final Builder builder )
    {
        this.node = builder.node;
        this.reorderedNodes = builder.reorderedNodes.build();
    }

    public Node getNode()
    {
        return node;
    }

    public List<Node> getReorderedNodes()
    {
        return reorderedNodes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Builder()
        {
        }

        private Node node;

        private ImmutableList.Builder<Node> reorderedNodes = ImmutableList.builder();

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder addReorderedNode(Node node) {
            reorderedNodes.add( node );
            return this;
        }

        public SortNodeResult build()
        {
            return new SortNodeResult( this );
        }
    }
}
