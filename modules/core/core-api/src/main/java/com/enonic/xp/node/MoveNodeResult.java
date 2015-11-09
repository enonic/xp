package com.enonic.xp.node;

import com.google.common.base.Preconditions;

public class MoveNodeResult
{
    private final Node sourceNode;

    private final Node targetNode;

    private MoveNodeResult( Builder builder )
    {
        this.sourceNode = builder.sourceNode;
        this.targetNode = builder.targetNode;
    }

    public Node getSourceNode()
    {
        return sourceNode;
    }

    public Node getTargetNode()
    {
        return targetNode;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Node sourceNode;

        private Node targetNode;

        private Builder()
        {
        }

        public Builder sourceNode( final Node sourceNode )
        {
            this.sourceNode = sourceNode;
            return this;
        }

        public Builder targetNode( final Node targetNode )
        {
            this.targetNode = targetNode;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( sourceNode, "sourceNode cannot be null" );
        }

        public MoveNodeResult build()
        {
            this.validate();
            return new MoveNodeResult( this );
        }
    }
}
