package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl.DslQueryParser;

class DslExpressionBuilder
{
    private DslExpressionBuilder()
    {
    }

    public static QueryBuilder build( final DslExpr dslExpr )
    {
        final PropertyTree expression = dslExpr.getExpression();

        final List<Property> properties = (List<Property>) expression.getRoot().getProperties();
        if ( properties.isEmpty() )
        {
            throw new IllegalArgumentException( "Query is empty" );
        }
        else if ( properties.size() > 1 )
        {
            throw new IllegalArgumentException(
                "Query allows only single root expression, but actual size is: " + expression.getTotalSize() );
        }
        return DslQueryParser.parseQuery( properties.get( 0 ) );
    }

}
