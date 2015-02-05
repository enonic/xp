package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;

public final class DeleteNodeByIdCommand
    extends AbstractDeleteNodeCommand
{
    private final NodeId nodeId;

    private DeleteNodeByIdCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
    }

    public Node execute()
    {
        final Context context = ContextAccessor.current();

        final Node node = doGetById( nodeId, false );

        if ( node != null )
        {
            deleteNodeWithChildren( node, context );
        }

        return node;
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
        extends AbstractDeleteNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        Builder()
        {
            super();
        }

        Builder( final AbstractNodeCommand source )
        {
            super( source );
        }


        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.nodeId );
        }

        public DeleteNodeByIdCommand build()
        {
            this.validate();
            return new DeleteNodeByIdCommand( this );
        }
    }

}
