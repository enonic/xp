package com.enonic.wem.repo.internal.elasticsearch.query.translator.builder;

import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.wem.repo.internal.elasticsearch.function.FunctionQueryBuilderException;
import com.enonic.wem.repo.internal.elasticsearch.function.GeoDistanceSortFunctionArguments;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.wem.repo.internal.index.IndexValueType;
import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;

class DynamicSortBuilderFactory
    extends AbstractBuilderFactory
{
    public DynamicSortBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public SortBuilder create( final DynamicOrderExpr orderExpr )
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

    private SortBuilder createGeoDistanceSort( final DynamicOrderExpr orderExpr )
    {
        final FunctionExpr function = orderExpr.getFunction();

        GeoDistanceSortFunctionArguments arguments = new GeoDistanceSortFunctionArguments( function.getArguments() );

        final String baseFieldName = arguments.getFieldName();

        final String queryFieldName = this.fieldNameResolver.resolve( baseFieldName, IndexValueType.GEO_POINT );

        GeoDistanceSortBuilder builder = new GeoDistanceSortBuilder( queryFieldName );
        builder.point( arguments.getLatitude(), arguments.getLongitude() );
        builder.order( SortOrder.valueOf( orderExpr.getDirection().toString() ) );

        return builder;
    }
}
