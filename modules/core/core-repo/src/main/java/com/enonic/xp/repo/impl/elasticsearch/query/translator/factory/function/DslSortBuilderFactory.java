package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

public class DslSortBuilderFactory
    extends AbstractBuilderFactory
{
    private static final String UNMAPPED_TYPE = "long";

    public DslSortBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public SortBuilder create( final DslOrderExpr orderExpr )
    {
        final String type = orderExpr.getType();

        if ( type != null && !"geoDistance".equals( type ) )
        {
            throw new IllegalArgumentException( "Not valid sort function: '" + type + "'" );
        }

        if ( "geoDistance".equals( type ) || orderExpr.getLat() != null )
        {
            return GeoDistanceSortFunction.create( orderExpr );
        }
        else
        {
            final String field = orderExpr.getField();

            final FieldSortBuilder fieldSortBuilder = new FieldSortBuilder( fieldNameResolver.resolveOrderByFieldName( field ) );

            if ( orderExpr.getDirection() != null )
            {
                fieldSortBuilder.order( SortOrder.valueOf( orderExpr.getDirection().name() ) );
            }
            fieldSortBuilder.unmappedType( UNMAPPED_TYPE );

            return fieldSortBuilder;
        }
    }
}
