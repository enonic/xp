package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;

public class TermExpressionBuilderTest
    extends BaseTestBuilderFactory
{
    @Test
    public void compare_eq_string()
        throws Exception
    {
        final String expected = load( "compare_eq_string.json" );

        final QueryBuilder query =
            TermExpressionBuilder.build( CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) ),
                                         new SearchQueryFieldNameResolver() );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void compare_eq_number()
        throws Exception
    {
        final String expected = load( "compare_eq_number.json" );

        final QueryBuilder query = TermExpressionBuilder.build( CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.number( 1 ) ),
                                                                new SearchQueryFieldNameResolver() );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void compare_eq_datetime()
        throws Exception
    {
        final String expected = load( "compare_eq_datetime.json" );

        final QueryBuilder query =
            TermExpressionBuilder.build( CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.instant( "2013-11-29T09:42:00Z" ) ),
                                         new SearchQueryFieldNameResolver() );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void compare_eq_geopoint()
        throws Exception
    {
        final String expected = load( "compare_eq_geopoint.json" );

        final QueryBuilder query =
            TermExpressionBuilder.build( CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.geoPoint( "59.9127300,10.746090" ) ),
                                         new SearchQueryFieldNameResolver() );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}
