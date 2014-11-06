package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.core.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.NodeQueryResult;

public class FindNodesByParentCommand
    extends AbstractNodeCommand
{
    private final FindNodesByParentParams params;

    private FindNodesByParentCommand( Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindNodesByParentResult execute()
    {
        final ChildOrder order = NodeChildOrderResolver.create().
            nodeDao( this.nodeDao ).
            workspaceService( this.queryService ).
            nodePath( params.getParentPath() ).
            childOrder( params.getChildOrder() ).
            build().
            resolve();

        final NodeQuery query = createByPathQuery( order );

        final NodeQueryResult nodeQueryResult = this.queryService.find( query, IndexContext.from( Context.current() ) );

        final Nodes nodes = doGetByIds( nodeQueryResult.getNodeIds(), true );

        return FindNodesByParentResult.create().
            nodes( nodes ).
            totalHits( nodeQueryResult.getTotalHits() ).
            hits( nodeQueryResult.getHits() ).
            build();
    }

    private NodeQuery createByPathQuery( final ChildOrder order )
    {
        return NodeQuery.create().
            parent( this.params.getParentPath() ).
            query( new QueryExpr( order.getOrderExpressions() ) ).
            from( params.getFrom() ).
            size( params.getSize() ).
            build();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private FindNodesByParentParams params;

        public Builder()
        {
            super();
        }

        public Builder params( FindNodesByParentParams params )
        {
            this.params = params;
            return this;
        }

        public FindNodesByParentCommand build()
        {
            return new FindNodesByParentCommand( this );
        }
    }
}
