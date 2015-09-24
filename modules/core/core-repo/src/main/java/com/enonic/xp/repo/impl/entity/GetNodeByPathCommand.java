package com.enonic.xp.repo.impl.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.index.IndexContext;

public class GetNodeByPathCommand
    extends AbstractNodeCommand
{
    private final NodePath path;

    private final boolean resolveHasChild;

    private GetNodeByPathCommand( final Builder builder )
    {
        super( builder );
        path = builder.path;
        resolveHasChild = builder.resolveHasChild;
    }

    public Node execute()
    {
        final Context context = ContextAccessor.current();

        final NodeVersionId currentVersion = this.queryService.get( path, IndexContext.from( context ) );

        if ( currentVersion == null )
        {
            return null;
        }

        final Node node = nodeDao.getByVersionId( currentVersion );

        return resolveHasChild ? NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( node ) : node;
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

        private boolean resolveHasChild = true;

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

        public Builder resolveHasChild( boolean resolveHasChild )
        {
            this.resolveHasChild = resolveHasChild;
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
