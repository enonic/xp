package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;

public class RangeExpressionBuilderTest
    extends BaseTestBuilderFactory
{

    @Test
    public void compare_gt_number()
        throws Exception
    {
        final String expected = load( "compare_gt_number.json" );

        final QueryBuilder query = RangeExpressionBuilder.build( CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.number( 3L ) ),
                                                                 new SearchQueryFieldNameResolver() );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }


    @Test
    public void compare_lt_number()
        throws Exception
    {
        final String expected = load( "compare_lt_number.json" );

        final QueryBuilder query = RangeExpressionBuilder.build( CompareExpr.lt( FieldExpr.from( "myField" ), ValueExpr.number( 3L ) ),
                                                                 new SearchQueryFieldNameResolver() );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void compare_gte_number()
        throws Exception
    {
        final String expected = load( "compare_gte_number.json" );

        final QueryBuilder query = RangeExpressionBuilder.build( CompareExpr.gte( FieldExpr.from( "myField" ), ValueExpr.number( 3L ) ),
                                                                 new SearchQueryFieldNameResolver() );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void compare_gte_instant()
        throws Exception
    {
        final String expected = load( "compare_gt_datetime.json" );

        final QueryBuilder query =
            RangeExpressionBuilder.build( CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.instant( "2013-11-29T11:00:00.000Z" ) ),
                                          new SearchQueryFieldNameResolver() );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void compare_gt_string()
        throws Exception
    {
        final String expected = load( "compare_gt_string.json" );

        final QueryBuilder query =
            RangeExpressionBuilder.build( CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.string( "myString" ) ),
                                          new SearchQueryFieldNameResolver() );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}

