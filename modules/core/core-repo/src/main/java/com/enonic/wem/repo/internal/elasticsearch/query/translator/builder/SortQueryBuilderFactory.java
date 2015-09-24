package com.enonic.wem.repo.internal.elasticsearch.query.translator.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.google.common.collect.Lists;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;

public class SortQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public SortQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    private static final boolean IGNORE_UNMAPPED = true;

    public List<SortBuilder> create( final Collection<OrderExpr> orderExpressions )
    {
        return doCreate( orderExpressions );
    }

    private List<SortBuilder> doCreate( final Collection<OrderExpr> orderExpressions )
    {
        if ( orderExpressions.isEmpty() )
        {
            return new ArrayList<>();
        }

        List<SortBuilder> sortBuilders = Lists.newArrayList();

        for ( final OrderExpr orderExpr : orderExpressions )
        {
            if ( orderExpr instanceof FieldOrderExpr )
            {
                sortBuilders.add( createFieldSortBuilder( (FieldOrderExpr) orderExpr ) );
            }
            else if ( orderExpr instanceof DynamicOrderExpr )
            {
                sortBuilders.add( new DynamicSortBuilderFactory( fieldNameResolver ).create( (DynamicOrderExpr) orderExpr ) );
            }
        }

        return sortBuilders;
    }

    private SortBuilder createFieldSortBuilder( final FieldOrderExpr fieldOrderExpr )
    {
        final FieldSortBuilder fieldSortBuilder =
            new FieldSortBuilder( fieldNameResolver.resolveOrderByFieldName( fieldOrderExpr.getField().getFieldPath() ) );
        fieldSortBuilder.order( SortOrder.valueOf( fieldOrderExpr.getDirection().name() ) );
        fieldSortBuilder.ignoreUnmapped( IGNORE_UNMAPPED );

        return fieldSortBuilder;
    }

}
