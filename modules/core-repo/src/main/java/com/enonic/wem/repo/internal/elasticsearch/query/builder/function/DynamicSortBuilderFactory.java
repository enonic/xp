package com.enonic.wem.repo.internal.elasticsearch.query.builder.function;

import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.wem.repo.internal.elasticsearch.function.FunctionQueryBuilderException;
import com.enonic.wem.repo.internal.elasticsearch.function.GeoDistanceSortFunctionArguments;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

public class DynamicSortBuilderFactory
{

    public static SortBuilder create( final DynamicOrderExpr orderExpr )
    {
        final FunctionExpr function = orderExpr.getFunction();

        final String functionName = function.getName();

        switch ( functionName )
        {
            case "geoDistance":
                return createGeoDistanceSort( orderExpr );
            default:
                throw new FunctionQueryBuilderException( "Not valid sort function: '" + functionName + "'" );
        }
    }

    private static SortBuilder createGeoDistanceSort( final DynamicOrderExpr orderExpr )
    {
        final FunctionExpr function = orderExpr.getFunction();

        GeoDistanceSortFunctionArguments arguments = new GeoDistanceSortFunctionArguments( function.getArguments() );

        final String baseFieldName = arguments.getFieldName();

        final String queryFieldName = IndexQueryFieldNameResolver.resolveGeoPointFieldName( baseFieldName );

        GeoDistanceSortBuilder builder = new GeoDistanceSortBuilder( queryFieldName );
        builder.point( arguments.getLatitude(), arguments.getLongitude() );
        builder.order( SortOrder.valueOf( orderExpr.getDirection().toString() ) );

        return builder;
    }
}
