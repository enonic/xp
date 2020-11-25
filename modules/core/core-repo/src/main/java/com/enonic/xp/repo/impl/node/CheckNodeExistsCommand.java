package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;

public class CheckNodeExistsCommand
    extends AbstractNodeCommand
{
    private final NodePath nodePath;

    private final boolean throwIfExists;

    private final NodeId skipThrowIfSameId;

    private CheckNodeExistsCommand( Builder builder )
    {
        super( builder );
        nodePath = builder.nodePath;
        throwIfExists = builder.throwIfExists;
        skipThrowIfSameId = builder.skipThrowIfSameId;
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
        final InternalContext context = InternalContext.from( ContextAccessor.current() );
        final NodeId found = nodeStorageService.getIdForPath( nodePath, context );

        if ( throwIfExists && found != null )
        {
            if ( skipThrowIfSameId == null )
            {
                throw new NodeAlreadyExistAtPathException( nodePath, context.getRepositoryId(), context.getBranch() );
            }
            else if ( !found.equals( skipThrowIfSameId ) )
            {
                throw new NodeAlreadyExistAtPathException( nodePath, context.getRepositoryId(), context.getBranch() );
            }
        }
        return found != null;
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath nodePath;

        private boolean throwIfExists;

        private NodeId skipThrowIfSameId;

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

        public Builder throwIfExists()
        {
            this.throwIfExists = true;
            return this;
        }

        public Builder skipThrowIfSameId( final NodeId skipThrowIfSameId )
        {
            this.skipThrowIfSameId = skipThrowIfSameId;
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
