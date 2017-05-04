package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;

public class LikeExpressionBuilderTest
    extends BaseTestBuilderFactory
{
    @Test
    public void compareLikeString()
        throws Exception
    {
        final String expected = load( "compare_like_string.json" );

        final QueryBuilder query =
            LikeExpressionBuilder.build( CompareExpr.like( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) ),
                                         new SearchQueryFieldNameResolver() );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );

    }
}
