package com.enonic.wem.core.entity;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.FieldSort;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.FindNodesByParentParams;
import com.enonic.wem.api.entity.FindNodesByParentResult;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.core.entity.index.NodeIndexDocumentFactory;
import com.enonic.wem.core.index.query.NodeQueryResult;
import com.enonic.wem.core.workspace.query.WorkspaceIdsQuery;

public class FindNodesByParentCommand
    extends AbstractFindNodeCommand
{
    private final FindNodesByParentParams params;

    private FindNodesByParentCommand( Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }

    public FindNodesByParentResult execute()
    {
        final NodeQuery query = createByPathQuery();

        final NodeQueryResult nodeQueryResult = this.queryService.find( query, this.context.getWorkspace() );

        final NodeVersionIds versions =
            this.workspaceService.getByVersionIds( new WorkspaceIdsQuery( this.context.getWorkspace(), nodeQueryResult.getEntityIds() ) );

        final Nodes nodes = nodeDao.getByVersionIds( versions );

        return FindNodesByParentResult.create().
            nodes( nodes ).
            totalHits( nodeQueryResult.getTotalHits() ).
            hits( nodeQueryResult.getHits() ).
            build();
    }

    private NodeQuery createByPathQuery()
    {
        final Set<OrderExpr> orderBys = Sets.newHashSet();

        for ( final FieldSort fieldSort : this.params.getSorting() )
        {
            final FieldOrderExpr orderByExpr = new FieldOrderExpr( new FieldExpr( fieldSort.getFieldName() ),
                                                                   OrderExpr.Direction.valueOf( fieldSort.getDirection().name() ) );
            orderBys.add( orderByExpr );
        }

        return NodeQuery.create().
            addQueryFilter( Filter.newValueQueryFilter().
                fieldName( NodeIndexDocumentFactory.PARENT_PATH_KEY ).
                add( Value.newString( params.getParentPath().toString() ) ).
                build() ).
            query( new QueryExpr( orderBys ) ).
            from( params.getFrom() ).
            size( params.getSize() ).
            build();
    }


    public static class Builder
        extends AbstractFindNodeCommand.Builder<Builder>
    {
        private FindNodesByParentParams params;

        public Builder( final Context context )
        {
            super( context );
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
