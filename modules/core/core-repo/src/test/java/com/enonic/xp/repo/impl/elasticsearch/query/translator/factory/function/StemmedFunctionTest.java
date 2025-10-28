package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StemmedFunctionTest
    extends BaseTestBuilderFactory
{
    @Test
    void test()
    {
        final String expected = load( "stemmed_function.json" );

        final QueryBuilder query = StemmedFunction.create(
            FunctionExpr.from( "stemmed", ValueExpr.string( "field" ), ValueExpr.string( "organize" ), ValueExpr.string( "OR" ),
                               ValueExpr.string( "en" ) ) );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}
