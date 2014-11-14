package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeVersionId;

public class GetNodeByIdCommand
    extends AbstractNodeCommand
{
    private final NodeId id;

    private final boolean resolveHasChild;

    private GetNodeByIdCommand( final Builder builder )
    {
        super( builder );
        id = builder.id;
        resolveHasChild = builder.resolveHasChild;
    }

    public Node execute()
    {
        final Context context = ContextAccessor.current();

        final NodeVersionId currentVersion = this.queryService.get( this.id, IndexContext.from( context ) );

        if ( currentVersion == null )
        {
            return null;
        }

        final Node node = nodeDao.getByVersionId( currentVersion );

        return !resolveHasChild ? node : NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( node );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private boolean resolveHasChild = true;

        private Builder()
        {
        }

        public Builder id( NodeId id )
        {
            this.id = id;
            return this;
        }

        public Builder resolveHasChild( final boolean resolveHasChild )
        {
            this.resolveHasChild = resolveHasChild;
            return this;
        }

        public GetNodeByIdCommand build()
        {
            return new GetNodeByIdCommand( this );
        }
    }
}
