package com.enonic.xp.repo.impl.node;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.Node;

public class DuplicateNodeResult
{
    private final Node node;

    private final List<Node> children;

    private DuplicateNodeResult( Builder builder )
    {
        this.node = builder.node;
        this.children = builder.children.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<Node> getChildren()
    {
        return children;
    }

    public Node getNode()
    {
        return node;
    }

    public static class Builder
    {
        private final ImmutableList.Builder<Node> children = ImmutableList.builder();

        private Node node;

        private Builder()
        {
        }


        public Builder node( final Node node )
        {
            this.node = node;
            return this;
        }

        public Builder addChild( final Node child )
        {
            this.children.add( child );
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( node, "node is required" );
        }

        public DuplicateNodeResult build()
        {
            validate();
            return new DuplicateNodeResult( this );
        }
    }
}
