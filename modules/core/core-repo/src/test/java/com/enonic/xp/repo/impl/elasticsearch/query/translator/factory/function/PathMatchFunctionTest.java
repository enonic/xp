package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;

public class PathMatchFunctionTest
    extends BaseTestBuilderFactory
{

    @Test
    public void two_arguments()
        throws Exception
    {
        final String expected = load( "pathMatch.json" );

        final QueryBuilder query =
            PathMatchFunction.create( FunctionExpr.from( "pathMatch", ValueExpr.string( "myPath" ), ValueExpr.string( "/fisk" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );

    }

    @Test
    public void with_minimum_should_match()
        throws Exception
    {
        final String expected = load( "pathMatch_minimum_match.json" );

        final QueryBuilder query = PathMatchFunction.create(
            FunctionExpr.from( "pathMatch", ValueExpr.string( "myPath" ), ValueExpr.string( "/fisk" ), ValueExpr.number( 3 ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );

    }
}