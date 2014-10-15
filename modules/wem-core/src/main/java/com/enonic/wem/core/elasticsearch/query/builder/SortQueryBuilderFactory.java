package com.enonic.wem.core.elasticsearch.query.builder;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.google.common.collect.Sets;

import com.enonic.wem.api.query.expr.DynamicOrderExpr;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.core.elasticsearch.query.builder.function.DynamicSortBuilderFactory;
import com.enonic.wem.core.index.query.IndexQueryFieldNameResolver;

public class SortQueryBuilderFactory
    extends AbstractQueryBuilderFactory
{
    private static final boolean IGNORE_UNMAPPED = true;

    public static Set<SortBuilder> create( final Collection<OrderExpr> orderExpressions )
    {
        Set<SortBuilder> sortBuilders = Sets.newHashSet();

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
            new FieldSortBuilder( IndexQueryFieldNameResolver.resolveOrderByFieldName( fieldOrderExpr.getField().getName() ) );
        fieldSortBuilder.order( SortOrder.valueOf( fieldOrderExpr.getDirection().name() ) );
        fieldSortBuilder.ignoreUnmapped( IGNORE_UNMAPPED );

        return fieldSortBuilder;
    }

}
