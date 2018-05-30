package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;

public class RangeFunctionTest
    extends BaseTestBuilderFactory
{

    @Test
    public void instant_range_no_includes()
        throws Exception
    {
        final String expected = load( "range_instant_no_include.json" );

        final QueryBuilder query = RangeFunction.create(
            FunctionExpr.from( "range", ValueExpr.string( "MyField" ), ValueExpr.instant( "1975-08-01T10:00Z" ),
                               ValueExpr.instant( "1975-08-01T10:00Z" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void instant_range_includes()
        throws Exception
    {
        final String expected = load( "range_instant_includes.json" );

        final QueryBuilder query = RangeFunction.create(
            FunctionExpr.from( "range", ValueExpr.string( "MyField" ), ValueExpr.instant( "1975-08-01T10:00Z" ),
                               ValueExpr.instant( "1975-08-01T10:00Z" ), ValueExpr.string( "true" ), ValueExpr.string( "false" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void instant_string_range_includes()
        throws Exception
    {
        final String expected = load( "range_instant_includes.json" );

        final QueryBuilder query = RangeFunction.create(
            FunctionExpr.from( "range", ValueExpr.string( "MyField" ), ValueExpr.string( "1975-08-01T10:00Z" ),
                               ValueExpr.string( "1975-08-01T10:00Z" ), ValueExpr.string( "true" ), ValueExpr.string( "false" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void string_range_includes()
        throws Exception
    {
        final String expected = load( "range_string_includes.json" );

        final QueryBuilder query = RangeFunction.create(
            FunctionExpr.from( "range", ValueExpr.string( "MyField" ), ValueExpr.string( "5.1.0" ), ValueExpr.string( "5.3.0" ),
                               ValueExpr.string( "true" ), ValueExpr.string( "false" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void numeric_range_includes()
        throws Exception
    {
        final String expected = load( "range_numeric_includes.json" );

        final QueryBuilder query = RangeFunction.create(
            FunctionExpr.from( "range", ValueExpr.string( "MyField" ), ValueExpr.number( 2.0 ), ValueExpr.number( 3.0 ),
                               ValueExpr.string( "true" ), ValueExpr.string( "false" ) ) );

        System.out.println( query );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

}