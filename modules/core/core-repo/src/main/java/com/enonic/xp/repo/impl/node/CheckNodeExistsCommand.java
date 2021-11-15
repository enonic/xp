package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.SearchPreference;
import com.enonic.xp.repo.impl.InternalContext;

public class CheckNodeExistsCommand
    extends AbstractNodeCommand
{
    private final NodePath nodePath;

    private final boolean throwIfExists;

    private final Mode mode;

    private CheckNodeExistsCommand( Builder builder )
    {
        super( builder );
        nodePath = builder.nodePath;
        throwIfExists = builder.throwIfExists;
        mode = builder.mode;
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
        final InternalContext context = InternalContext.create( ContextAccessor.current() )
            .searchPreference( Mode.ACCURACY.equals( mode ) ? SearchPreference.PRIMARY : SearchPreference.LOCAL )
            .build();

        if ( Mode.ACCURACY.equals( mode ) )
        {
            RefreshCommand.create().indexServiceInternal( this.indexServiceInternal ).refreshMode( RefreshMode.STORAGE ).build().execute();
        }

        final NodeId found = nodeStorageService.getIdForPath( nodePath, context );

        if ( found != null && throwIfExists )
        {
            throw new NodeAlreadyExistAtPathException( nodePath, context.getRepositoryId(), context.getBranch() );
        }

        return found != null;
    }

    public enum Mode
    {
        SPEED, ACCURACY
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath nodePath;

        private boolean throwIfExists;

        private Mode mode = Mode.ACCURACY;

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

        public Builder mode( final Mode mode )
        {
            this.mode = mode;
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
