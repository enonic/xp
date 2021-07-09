package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query.dsl;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.dsl.DslFulltextBuilder;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.dsl.DslNgramBuilder;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.dsl.DslStemmedBuilder;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

public class DslExpressionBuilder
{
    private static final String BOOLEAN = "boolean";

    public static QueryBuilder build( final DslExpr expr, final QueryFieldNameResolver resolver )
    {
        final PropertyTree expression = expr.getExpression();

        for ( final Property property : expression.getProperties() )
        {
            switch ( property.getName() )
            {
                case BOOLEAN:
                    return parseBooleanExpression( property.getSet() );
                default:
                    throw new IllegalStateException( "Invalid property: " + property.getName() );

            }
        }

        return QueryBuilders.boolQuery();
    }


    private static QueryBuilder parseExpression( final PropertySet set )
    {
        for ( final Property property : set.getProperties() )
        {
            switch ( property.getName() )
            {
                case "fulltext":
                {
                    return new DslFulltextBuilder( property.getSet() ).create();
                }
                case "ngram":
                {
                    return new DslNgramBuilder( property.getSet() ).create();
                }
                case "stemmed":
                {
                    return new DslStemmedBuilder( property.getSet() ).create();
                }
                /*
                case "range":
                {
                    return RangeFunction.create( function );
                }
                case "pathMatch":                {
                    return PathMatchFunction.create( function );
                }
               */
                default:
                    throw new IllegalArgumentException( "Function '" + property.getName() + "' is not supported" );

            }
        }
        return QueryBuilders.boolQuery();
    }

    private static QueryBuilder parseBooleanExpression( final PropertySet set )
    {
        final BoolQueryBuilder builder = QueryBuilders.boolQuery();
        for ( final Property property : set.getProperties() )
        {
            switch ( property.getName() )
            {
                case "must":
                    return builder.must( parseExpression( property.getSet() ) );
                case "should":
                    return builder.should( parseExpression( property.getSet() ) );
                case "mustNot":
                    return builder.mustNot( parseExpression( property.getSet() ) );
                default:
                    throw new IllegalStateException( "Invalid boolean expression: " + property.getName() );
            }
        }
        return builder;
    }
}
