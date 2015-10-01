package com.enonic.xp.repo.impl.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.InternalContext;

public class GetNodesByIdsCommand
    extends AbstractNodeCommand
{
    private final NodeIds ids;

    private GetNodesByIdsCommand( final Builder builder )
    {
        super( builder );
        this.ids = builder.ids;
    }

    public Nodes execute()
    {
        return this.storageService.get( ids, InternalContext.from( ContextAccessor.current() ) );
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
        private NodeIds ids;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder ids( NodeIds ids )
        {
            this.ids = ids;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.ids );
        }

        public GetNodesByIdsCommand build()
        {
            this.validate();
            return new GetNodesByIdsCommand( this );
        }
    }
}
