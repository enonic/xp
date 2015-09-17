package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

public class GetNodeByPathCommand
    extends AbstractNodeCommand
{
    private final NodePath path;

    private GetNodeByPathCommand( final Builder builder )
    {
        super( builder );
        path = builder.path;
    }

    public Node execute()
    {
        final Context context = ContextAccessor.current();

        return this.storageService.get( path, InternalContext.from( context ) );
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
        private NodePath path;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodePath( NodePath path )
        {
            this.path = path;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.path );
        }

        public GetNodeByPathCommand build()
        {
            this.validate();
            return new GetNodeByPathCommand( this );
        }
    }
}
