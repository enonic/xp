package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SearchPreference;

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

        final NodeBranchEntry found = nodeStorageService.getBranchNodeVersion( nodePath, context );

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
            Objects.requireNonNull( this.nodePath, "nodePath is required" );
        }

        public CheckNodeExistsCommand build()
        {
            validate();
            return new CheckNodeExistsCommand( this );
        }
    }
}
