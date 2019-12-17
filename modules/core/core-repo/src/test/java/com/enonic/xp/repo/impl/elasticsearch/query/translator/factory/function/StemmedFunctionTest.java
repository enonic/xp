package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.lang.reflect.Field;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;
import com.enonic.xp.repo.impl.index.IndexStemmedController;
import com.enonic.xp.repo.impl.index.StemmedIndexValueType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StemmedFunctionTest
    extends BaseTestBuilderFactory
{
    @BeforeEach
    public void init()
        throws Exception
    {
        Field analyzers = IndexStemmedController.class.getDeclaredField( "SUPPORTED_ANALYZERS" );
        Field indexValueTypes = IndexStemmedController.class.getDeclaredField( "SUPPORTED_INDEX_VALUE_TYPES" );

        analyzers.setAccessible( true );
        indexValueTypes.setAccessible( true );

        analyzers.set( null, ImmutableMap.of( "en", "language_analyzer_en" ) );
        indexValueTypes.set( null, ImmutableMap.of( "en", new StemmedIndexValueType( "en" ) ) );
    }

    @Test
    public void test()
        throws Exception
    {
        final String expected = load( "stemmed_function.json" );

        final QueryBuilder query = StemmedFunction.create(
            FunctionExpr.from( "stemmed", ValueExpr.string( "field" ), ValueExpr.string( "organize" ), ValueExpr.string( "OR" ),
                               ValueExpr.string( "en" ) ) );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}
