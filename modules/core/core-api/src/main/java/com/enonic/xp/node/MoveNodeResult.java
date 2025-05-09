package com.enonic.xp.node;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class MoveNodeResult
{
    private final List<MovedNode> movedNodes;

    private MoveNodeResult( final Builder builder )
    {
        this.movedNodes = builder.movedNodes.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<MovedNode> getMovedNodes()
    {
        return movedNodes;
    }

    public static class MovedNode
    {
        private final NodePath previousPath;

        private final Node node;

        private MovedNode( final Builder builder )
        {
            this.previousPath = builder.previousPath;
            this.node = builder.node;
        }

        public static Builder create()
        {
            return new Builder();
        }

        public NodePath getPreviousPath()
        {
            return previousPath;
        }

        public Node getNode()
        {
            return node;
        }

        public static class Builder
        {
            private NodePath previousPath;

            private Node node;

            public Builder node( final Node node )
            {
                this.node = node;
                return this;
            }

            public Builder previousPath( final NodePath path )
            {
                this.previousPath = path;
                return this;
            }

            private void validate()
            {
                Preconditions.checkNotNull( previousPath, "previousPath must be set" );
                Preconditions.checkNotNull( node, "node must be set" );
            }

            public MovedNode build()
            {
                validate();
                return new MovedNode( this );
            }
        }
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<MovedNode> movedNodes = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder addMovedNode( final MovedNode node )
        {
            this.movedNodes.add( node );
            return this;
        }

        public MoveNodeResult build()
        {
            return new MoveNodeResult( this );
        }
    }
}
