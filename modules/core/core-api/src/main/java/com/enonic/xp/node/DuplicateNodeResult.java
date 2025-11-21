package com.enonic.xp.node;

import java.util.Objects;

public class DuplicateNodeResult
{
    private final Node node;

    private final Nodes children;

    private DuplicateNodeResult( Builder builder )
    {
        this.node = builder.node;
        this.children = builder.children.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Nodes getChildren()
    {
        return children;
    }

    public Node getNode()
    {
        return node;
    }

    public static class Builder
    {
        private final Nodes.Builder children = Nodes.create();

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
