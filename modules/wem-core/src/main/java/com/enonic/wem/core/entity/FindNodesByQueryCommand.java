package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.core.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.NodeQueryResult;
import com.enonic.wem.core.workspace.WorkspaceContext;

public class FindNodesByQueryCommand
    extends AbstractFindNodeCommand
{
    private final NodeQuery query;

    private FindNodesByQueryCommand( Builder builder )
    {
        super( builder );
        query = builder.query;
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }

    public FindNodesByQueryResult execute()
    {
        final NodeQueryResult nodeQueryResult = queryService.find( query, IndexContext.from( this.context ) );

        final NodeVersionIds versions =
            workspaceService.getByVersionIds( nodeQueryResult.getEntityIds(), WorkspaceContext.from( context ) );

        final Nodes nodes = NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            context( this.context ).
            build().
            resolve( nodeDao.getByVersionIds( versions ) );

        return FindNodesByQueryResult.create().
            hits( nodeQueryResult.getHits() ).
            totalHits( nodeQueryResult.getTotalHits() ).
            aggregations( nodeQueryResult.getAggregations() ).
            nodes( nodes ).
            build();
    }

    public static final class Builder
        extends AbstractFindNodeCommand.Builder<Builder>
    {
        private NodeQuery query;

        private Builder( final Context context )
        {
            super( context );
        }

        public Builder query( NodeQuery query )
        {
            this.query = query;
            return this;
        }

        public FindNodesByQueryCommand build()
        {
            return new FindNodesByQueryCommand( this );
        }
    }
}
