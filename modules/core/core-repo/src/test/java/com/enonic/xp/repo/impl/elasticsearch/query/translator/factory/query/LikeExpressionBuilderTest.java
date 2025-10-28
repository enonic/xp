package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LikeExpressionBuilderTest
    extends BaseTestBuilderFactory
{
    @Test
    void compareLikeString()
    {
        final String expected = load( "compare_like_string.json" );

        final QueryBuilder query =
            LikeExpressionBuilder.build( CompareExpr.like( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) ),
                                         SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );

    }
}
