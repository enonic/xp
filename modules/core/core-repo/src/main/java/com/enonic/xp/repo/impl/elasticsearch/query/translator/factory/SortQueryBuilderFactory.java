package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.DynamicSortBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

public class SortQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public SortQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

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

        List<SortBuilder> sortBuilders = new ArrayList<>();

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
        final String fieldName = fieldNameResolver.resolveOrderByFieldName( fieldOrderExpr.getField().getFieldPath() );
        final FieldSortBuilder fieldSortBuilder = new FieldSortBuilder( fieldName );
        fieldSortBuilder.order( SortOrder.valueOf( fieldOrderExpr.getDirection().name() ) );

        if ( !"_score".equals( fieldName ) )
        {
            fieldSortBuilder.unmappedType( "long" );
        }

        return fieldSortBuilder;
    }

}
