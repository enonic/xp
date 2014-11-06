package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.core.index.IndexContext;

public class GetNodesByIdsCommand
    extends AbstractNodeCommand
{
    private final NodeIds ids;

    private final boolean resolveHasChild;

    private GetNodesByIdsCommand( final Builder builder )
    {
        super( builder );
        ids = builder.ids;
        resolveHasChild = builder.resolveHasChild;
    }

    public Nodes execute()
    {
        final NodeVersionIds versionIds = this.queryService.get( ids, IndexContext.from( Context.current() ) );

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
        private NodeIds ids;

        private boolean resolveHasChild;

        private Builder()
        {
        }

        public Builder ids( NodeIds ids )
        {
            this.ids = ids;
            return this;
        }

        public Builder resolveHasChild( boolean resolveHasChild )
        {
            this.resolveHasChild = resolveHasChild;
            return this;
        }

        public GetNodesByIdsCommand build()
        {
            return new GetNodesByIdsCommand( this );
        }
    }
}
