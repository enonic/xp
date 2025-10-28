package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NGramFunctionTest
    extends BaseTestBuilderFactory
{
    @Test
    void two_arguments()
        throws Exception
    {
        final String expected = load( "ngram_two_arguments.json" );

        final QueryBuilder query = NGramFunction.create(
            FunctionExpr.from( "nGram", ValueExpr.string( "fiskebolle" ), ValueExpr.string( "Her er teksten min" ) ) );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }


    @Test
    void override_operator()
    {
        final String expected = load( "ngram_override_operator.json" );

        final QueryBuilder query = NGramFunction.create(
            FunctionExpr.from( "nGram", ValueExpr.string( "fiskebolle" ), ValueExpr.string( "Her er teksten min" ),
                               ValueExpr.string( "AND" ) ) );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    void set_analyzer()
    {
        final String expected = load( "ngram_set_analyzer.json" );

        final QueryBuilder query = NGramFunction.create(
            FunctionExpr.from( "nGram", ValueExpr.string( "fiskebolle" ), ValueExpr.string( "Her er teksten min" ),
                               ValueExpr.string( "AND" ), ValueExpr.string( "MyNewAnalyzer" ) ) );

        System.out.println( query );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}
