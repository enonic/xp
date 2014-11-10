package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.core.index.IndexContext;

public class GetNodeByPathCommand
    extends AbstractNodeCommand
{
    private NodePath path;

    private boolean resolveHasChild;

    private GetNodeByPathCommand( final Builder builder )
    {
        super( builder );
        path = builder.path;
        resolveHasChild = builder.resolveHasChild;
    }

    public Node execute()
    {
        final Context context = Context.current();

        final NodeVersionId currentVersion = this.queryService.get( path, IndexContext.from( context ) );

        if ( currentVersion == null )
        {
            return null;
        }

        final Node node = nodeDao.getByVersionId( currentVersion );

        return resolveHasChild ? NodeHasChildResolver.create().
            workspaceService( this.queryService ).
            build().
            resolve( node ) : node;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath path;

        private boolean resolveHasChild = true;

        private Builder()
        {
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

        public GetNodeByPathCommand build()
        {
            return new GetNodeByPathCommand( this );
        }
    }
}
