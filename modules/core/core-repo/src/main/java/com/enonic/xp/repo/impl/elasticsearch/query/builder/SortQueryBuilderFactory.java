package com.enonic.xp.repo.impl.elasticsearch.query.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.google.common.collect.Lists;

import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.repo.impl.elasticsearch.query.builder.function.DynamicSortBuilderFactory;
import com.enonic.xp.repo.impl.index.query.IndexQueryFieldNameResolver;

public class SortQueryBuilderFactory
    extends AbstractQueryBuilderFactory
{
    private static final boolean IGNORE_UNMAPPED = true;

    public static List<SortBuilder> create( final OrderExpressions orderExpressions )
    {
        return doCreate( orderExpressions.getSet() );
    }

    public static List<SortBuilder> create( final Collection<OrderExpr> orderExpressions )
    {
        return doCreate( orderExpressions );
    }

    private static List<SortBuilder> doCreate( final Collection<OrderExpr> orderExpressions )
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
                sortBuilders.add( DynamicSortBuilderFactory.create( (DynamicOrderExpr) orderExpr ) );
            }
        }

        return sortBuilders;
    }

    private static SortBuilder createFieldSortBuilder( final FieldOrderExpr fieldOrderExpr )
    {
        final FieldSortBuilder fieldSortBuilder =
            new FieldSortBuilder( IndexQueryFieldNameResolver.resolveOrderByFieldName( fieldOrderExpr.getField().getFieldPath() ) );
        fieldSortBuilder.order( SortOrder.valueOf( fieldOrderExpr.getDirection().name() ) );
        fieldSortBuilder.ignoreUnmapped( IGNORE_UNMAPPED );

        return fieldSortBuilder;
    }

}
