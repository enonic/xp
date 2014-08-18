package com.enonic.wem.core.entity;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.FieldSort;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.FindNodesByParentParams;
import com.enonic.wem.api.entity.FindNodesByParentResult;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.index.NodeIndexDocumentFactory;
import com.enonic.wem.core.index.query.NodeQueryResult;
import com.enonic.wem.core.index.query.QueryService;

public class FindNodesByParentCommand
{
    private final QueryService queryService;

    private final FindNodesByParentParams params;

    private final Context context;

    private final NodeDao nodeDao;

    private FindNodesByParentCommand( Builder builder )
    {
        queryService = builder.queryService;
        params = builder.params;
        context = builder.context;
        nodeDao = builder.nodeDao;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindNodesByParentResult execute()
    {
        final NodeQuery query = createByPathQuery();

        final NodeQueryResult queryResult = this.queryService.find( query, this.context.getWorkspace() );

        final EntityIds entityIds = queryResult.getEntityIds();

        final Nodes nodes = nodeDao.getByIds( entityIds, this.context.getWorkspace() );

        return FindNodesByParentResult.create().
            nodes( nodes ).
            totalHits( queryResult.getTotalHits() ).
            hits( queryResult.getHits() ).
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

        return NodeQuery.newNodeQuery().
            addQueryFilter( Filter.newValueQueryFilter().
                fieldName( NodeIndexDocumentFactory.PARENT_PATH_KEY ).
                add( Value.newString( params.getParentPath().toString() ) ).
                build() ).
            query( new QueryExpr( orderBys ) ).
            from( params.getFrom() ).
            size( params.getSize() ).
            build();
    }


    public static final class Builder
    {
        private QueryService queryService;

        private FindNodesByParentParams params;

        private Context context;

        private NodeDao nodeDao;

        private Builder()
        {
        }

        public Builder queryService( QueryService queryService )
        {
            this.queryService = queryService;
            return this;
        }

        public Builder params( FindNodesByParentParams params )
        {
            this.params = params;
            return this;
        }

        public Builder context( Context context )
        {
            this.context = context;
            return this;
        }

        public Builder nodeDao( NodeDao nodeDao )
        {
            this.nodeDao = nodeDao;
            return this;
        }

        public FindNodesByParentCommand build()
        {
            return new FindNodesByParentCommand( this );
        }
    }
}
