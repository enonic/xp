package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeState;

public class SetNodeStateCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodeState nodeState;

    private SetNodeStateCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.nodeState = builder.nodeState;
    }

    public Node execute()
    {
        final Node node = doGetById( this.nodeId, false );

        final Node updatedNode = Node.newNode( node ).
            nodeState( this.nodeState ).
            build();

        updateNodeMetadata( updatedNode );

        return updatedNode;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeState getNodeState()
    {
        return nodeState;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private NodeState nodeState;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodeState( final NodeState nodeState )
        {
            this.nodeState = nodeState;
            return this;
        }

        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeId );
            Preconditions.checkNotNull( nodeState );
        }

        public SetNodeStateCommand build()
        {
            this.validate();
            return new SetNodeStateCommand( this );
        }
    }
}
