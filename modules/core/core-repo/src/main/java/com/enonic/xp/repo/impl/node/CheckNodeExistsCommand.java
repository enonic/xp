package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;

public class CheckNodeExistsCommand
    extends AbstractNodeCommand
{
    private final NodePath nodePath;

    private final NodeId nodeId;

    private CheckNodeExistsCommand( final Builder builder )
    {
        super( builder );
        nodePath = builder.nodePath;
        nodeId = builder.nodeId;
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
        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );

        if ( nodeId != null )
        {
            final NodeBranchEntry branchNodeVersion = this.storageService.getBranchNodeVersion( nodeId, internalContext );
            return branchNodeVersion != null;
        }

        final NodeId idForPath = this.storageService.getIdForPath( nodePath, internalContext );
        return idForPath != null;
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath nodePath;

        private NodeId nodeId;

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

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkArgument( this.nodePath != null || this.nodeId != null, "NodePath or NodeId must be set" );
        }

        public CheckNodeExistsCommand build()
        {
            validate();
            return new CheckNodeExistsCommand( this );
        }
    }
}
