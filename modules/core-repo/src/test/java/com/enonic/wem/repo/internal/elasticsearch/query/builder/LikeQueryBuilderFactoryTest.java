package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;

public class LikeQueryBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    @Test
    public void compareLikeString()
        throws Exception
    {
        final String expected = load( "compare_like_string.json" );

        final QueryBuilder query =
            LikeQueryBuilderFactory.create( CompareExpr.like( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );

    }
}
