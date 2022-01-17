package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.xp.query.expr.DslOrderExpr;
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

        GeoDistanceSortBuilder builder = new GeoDistanceSortBuilder( queryFieldName );
        builder.point( arguments.getLatitude(), arguments.getLongitude() );
        if ( orderExpr.getDirection() != null )
        {
            builder.order( SortOrder.valueOf( orderExpr.getDirection().name() ) );
        }
        if ( arguments.getUnit() != null )
        {
            builder.unit( DistanceUnit.fromString( arguments.getUnit() ) );
        }

        return builder;
    }

    public static SortBuilder create( final DslOrderExpr orderExpr )
    {
        final String queryFieldName = new SearchQueryFieldNameResolver().resolve( orderExpr.getField(), IndexValueType.GEO_POINT );

        GeoDistanceSortBuilder builder = new GeoDistanceSortBuilder( queryFieldName );
        builder.point( orderExpr.getLat(), orderExpr.getLon() );
        if ( orderExpr.getDirection() != null )
        {
            builder.order( SortOrder.valueOf( orderExpr.getDirection().toString() ) );
        }

        if ( orderExpr.getUnit() != null )
        {
            builder.unit( DistanceUnit.fromString( orderExpr.getUnit() ) );
        }

        return builder;
    }

}
