package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.function;

import org.elasticsearch.search.sort.SortBuilder;

import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.AbstractBuilderFactory;

public class DynamicSortBuilderFactory
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
                return GeoDistanceSortFunction.create( orderExpr );
            default:
                throw new FunctionQueryBuilderException( "Not valid sort function: '" + functionName + "'" );
        }
    }


}
