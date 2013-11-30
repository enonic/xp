package com.enonic.wem.core.index.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.core.index.query.function.FulltextQueryBuilderFactory;
import com.enonic.wem.query.expr.DynamicConstraintExpr;
import com.enonic.wem.query.expr.FunctionExpr;

public class DynamicQueryFactory
{
    public QueryBuilder create( final DynamicConstraintExpr constraintExpr )
    {
        final FunctionExpr function = constraintExpr.getFunction();

        if ( function == null )
        {
            return null;
        }

        final String functioName = function.getName();

        if ( "fulltext".equals( functioName ) )
        {
            return FulltextQueryBuilderFactory.create( function );
        }

        throw new UnsupportedOperationException( "Function '" + functioName + "' is not supported" );

    }

}
