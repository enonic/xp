package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.function;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.BaseTestBuilderFactory;

public class InboundDependenciesFunctionTest
    extends BaseTestBuilderFactory
{

    @Test
    public void two_arguments()
        throws Exception
    {
        final String expected = load( "inboundDependencies_function_test.json" );

        final QueryBuilder query = InboundDependenciesFunction.create(
            FunctionExpr.from( "inboundDependencies", ValueExpr.string( "_references" ), ValueExpr.string( "some-content-id" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );

    }
}