package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;

public class NGramFunctionTest
    extends BaseTestBuilderFactory
{
    @Test
    public void two_arguments()
        throws Exception
    {
        final String expected = load( "ngram_two_arguments.json" );

        final QueryBuilder query = NGramFunction.create(
            FunctionExpr.from( "nGram", ValueExpr.string( "fiskebolle" ), ValueExpr.string( "Her er teksten min" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }


    @Test
    public void override_operator()
        throws Exception
    {
        final String expected = load( "ngram_override_operator.json" );

        final QueryBuilder query = NGramFunction.create(
            FunctionExpr.from( "nGram", ValueExpr.string( "fiskebolle" ), ValueExpr.string( "Her er teksten min" ),
                               ValueExpr.string( "AND" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void set_analyzer()
        throws Exception
    {
        final String expected = load( "ngram_set_analyzer.json" );

        final QueryBuilder query = NGramFunction.create(
            FunctionExpr.from( "nGram", ValueExpr.string( "fiskebolle" ), ValueExpr.string( "Her er teksten min" ),
                               ValueExpr.string( "AND" ), ValueExpr.string( "MyNewAnalyzer" ) ) );

        System.out.println( query );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}