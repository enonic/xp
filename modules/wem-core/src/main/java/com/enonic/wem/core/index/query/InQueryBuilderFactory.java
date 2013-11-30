package com.enonic.wem.core.index.query;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.query.expr.CompareExpr;
import com.enonic.wem.query.expr.ValueExpr;

public class InQueryBuilderFactory
{

    public QueryBuilder create( final CompareExpr compareExpr )
    {

        final String queryFieldName = IndexQueryFieldNameResolver.resolveStringFieldName( compareExpr );

        final List<ValueExpr> values = compareExpr.getValues();

        if ( values == null || values.size() == 0 )
        {
            throw new IndexQueryBuilderException( "Cannot build empty 'IN' statements" );
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for ( ValueExpr value : values )
        {
            boolQuery.should( new TermQueryBuilderFactory().create( queryFieldName, value.getValue() ) );
        }

        return boolQuery;
    }

}
