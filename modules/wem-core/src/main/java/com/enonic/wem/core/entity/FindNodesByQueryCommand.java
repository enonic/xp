package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.FindNodesByQueryResult;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.core.index.query.NodeQueryResult;

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
        final NodeQueryResult nodeQueryResult = queryService.find( query, this.context.getWorkspace() );

        final Nodes nodes = nodeDao.getByIds( nodeQueryResult.getEntityIds(), this.context.getWorkspace() );

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
