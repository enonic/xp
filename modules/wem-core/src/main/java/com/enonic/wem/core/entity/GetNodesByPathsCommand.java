package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.core.index.IndexContext;

public class GetNodesByPathsCommand
    extends AbstractNodeCommand
{

    private final NodePaths paths;

    private final boolean resolveHasChild;

    private GetNodesByPathsCommand( Builder builder )
    {
        super( builder );
        paths = builder.paths;
        resolveHasChild = builder.resolveHasChild;
    }

    public Nodes execute()
    {
        final NodeVersionIds versionIds = this.queryService.get( paths, IndexContext.from( Context.current() ) );

        return resolveHasChild ? NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionIds( versionIds ) ) : nodeDao.getByVersionIds( versionIds );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePaths paths;

        private boolean resolveHasChild = true;

        private Builder()
        {
        }

        public Builder paths( NodePaths paths )
        {
            this.paths = paths;
            return this;
        }

        public Builder resolveHasChild( boolean resolveHasChild )
        {
            this.resolveHasChild = resolveHasChild;
            return this;
        }

        public GetNodesByPathsCommand build()
        {
            return new GetNodesByPathsCommand( this );
        }
    }
}
