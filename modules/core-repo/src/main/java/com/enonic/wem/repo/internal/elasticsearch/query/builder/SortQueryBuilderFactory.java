package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.google.common.collect.Sets;

import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.function.DynamicSortBuilderFactory;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

public class SortQueryBuilderFactory
    extends AbstractQueryBuilderFactory
{
    private static final boolean IGNORE_UNMAPPED = true;

    public static Set<SortBuilder> create( final OrderExpressions orderExpressions )
    {
        return doCreate( orderExpressions.getSet() );
    }

    public static Set<SortBuilder> create( final Collection<OrderExpr> orderExpressions )
    {
        return doCreate( orderExpressions );
    }

    private static Set<SortBuilder> doCreate( final Collection<OrderExpr> orderExpressions )
    {
        if ( orderExpressions.isEmpty() )
        {
            return new HashSet<>();
        }

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
            new FieldSortBuilder( IndexQueryFieldNameResolver.resolveOrderByFieldName( fieldOrderExpr.getField().getFieldPath() ) );
        fieldSortBuilder.order( SortOrder.valueOf( fieldOrderExpr.getDirection().name() ) );
        fieldSortBuilder.ignoreUnmapped( IGNORE_UNMAPPED );

        return fieldSortBuilder;
    }

}
