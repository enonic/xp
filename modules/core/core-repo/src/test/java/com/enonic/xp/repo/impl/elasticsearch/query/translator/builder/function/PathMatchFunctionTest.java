package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.function;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.BaseTestBuilderFactory;

public class PathMatchFunctionTest
    extends BaseTestBuilderFactory
{

    @Test
    public void create()
        throws Exception
    {
        final String expected = load( "pathMatch.json" );

        final QueryBuilder query =
            PathMatchFunction.create( FunctionExpr.from( "pathMatch", ValueExpr.string( "myPath" ), ValueExpr.string( "/fisk" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );

    }
}