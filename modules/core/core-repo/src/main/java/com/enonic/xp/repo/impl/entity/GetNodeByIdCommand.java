package com.enonic.xp.repo.impl.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;

public class GetNodeByIdCommand
    extends AbstractNodeCommand
{
    private final NodeId id;

    private GetNodeByIdCommand( final Builder builder )
    {
        super( builder );
        id = builder.id;
    }

    public Node execute()
    {
        final Context context = ContextAccessor.current();

        final Node node = this.storageService.get( id, InternalContext.from( context ) );

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

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder id( NodeId id )
        {
            this.id = id;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.id );
        }

        public GetNodeByIdCommand build()
        {
            this.validate();
            return new GetNodeByIdCommand( this );
        }
    }
}
