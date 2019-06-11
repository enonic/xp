package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.InternalContext;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class CheckNodeExistsCommand
    extends AbstractNodeCommand
{
    private final NodePath nodePath;

    private CheckNodeExistsCommand( Builder builder )
    {
        super( builder );
        nodePath = builder.nodePath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public boolean execute()
    {
        final NodeId idForPath = this.nodeStorageService.getIdForPath( nodePath, InternalContext.from( ContextAccessor.current() ) );

        return idForPath != null;
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath nodePath;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }


        public Builder nodePath( NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.nodePath, "Nodepath must be set" );
        }

        public CheckNodeExistsCommand build()
        {
            validate();
            return new CheckNodeExistsCommand( this );
        }
    }
}
