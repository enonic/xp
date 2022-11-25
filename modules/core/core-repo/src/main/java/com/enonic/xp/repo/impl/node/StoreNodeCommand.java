package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.InternalContext;

public class StoreNodeCommand
    extends AbstractNodeCommand
{
    private final Node node;

    private StoreNodeCommand( final Builder builder )
    {
        super( builder );
        this.node = builder.node;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public Node execute()
    {
        return this.nodeStorageService.store( this.node, InternalContext.from( ContextAccessor.current() ) );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Node node;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.node );
        }

        public StoreNodeCommand build()
        {
            this.validate();
            return new StoreNodeCommand( this );
        }
    }


}
