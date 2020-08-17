package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class GeoDistanceSortFunction
{
    public static SortBuilder create( final DynamicOrderExpr orderExpr )
    {
        final FunctionExpr function = orderExpr.getFunction();

        GeoDistanceSortFunctionArguments arguments = new GeoDistanceSortFunctionArguments( function.getArguments() );

        final String baseFieldName = arguments.getFieldName();

        final String queryFieldName = new SearchQueryFieldNameResolver().resolve( baseFieldName, IndexValueType.GEO_POINT );

        final GeoDistanceSortBuilder builder = new GeoDistanceSortBuilder( queryFieldName, arguments.getLatitude(), arguments.getLongitude() );
        builder.order( SortOrder.valueOf( orderExpr.getDirection().toString() ) );

        if ( arguments.getUnit() != null )
        {
            builder.unit( DistanceUnit.fromString( arguments.getUnit() ) );
        }

        return builder;
    }

}
