package com.enonic.wem.core.elasticsearch.query.builder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.api.query.expr.CompareExpr;
import com.enonic.wem.api.query.expr.ValueExpr;
import com.enonic.wem.core.index.query.IndexQueryBuilderException;
import com.enonic.wem.core.index.query.IndexQueryFieldNameResolver;

public class InQueryBuilderFactory
{

    public static QueryBuilder create( final CompareExpr compareExpr )
    {

        final String queryFieldName = IndexQueryFieldNameResolver.resolveStringFieldName( compareExpr.getField().getName() );

        final List<ValueExpr> values = compareExpr.getValues();

        if ( values == null || values.size() == 0 )
        {
            throw new IndexQueryBuilderException( "Cannot build empty 'IN' statements" );
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for ( ValueExpr value : values )
        {
            boolQuery.should( TermQueryBuilderFactory.create( queryFieldName, value.getValue() ) );
        }

        return boolQuery;
    }

}
