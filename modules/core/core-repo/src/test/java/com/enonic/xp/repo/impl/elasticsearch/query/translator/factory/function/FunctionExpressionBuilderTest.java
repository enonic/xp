package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionExpressionBuilderTest
{
    @Test
    public void test_fulltext()
    {
        List<ValueExpr> arguments =
            List.of( ValueExpr.string( "myField" ), ValueExpr.string( "mySearchString" ), ValueExpr.string( "OR" ) );

        final QueryBuilder fulltext = FunctionExpressionBuilder.build( new FunctionExpr( "fulltext", arguments ) );

        assertTrue( fulltext instanceof SimpleQueryStringBuilder );
    }

    @Test
    public void test_ngram()
    {
        final QueryBuilder nGram = FunctionExpressionBuilder.build(
            FunctionExpr.from( "ngram", ValueExpr.string( "fiskebolle" ), ValueExpr.string( "Her er teksten min" ) ) );

        assertTrue( nGram instanceof SimpleQueryStringBuilder );
    }

    @Test
    public void test_path_match()
    {
        final QueryBuilder pathMatch = FunctionExpressionBuilder.build(
            FunctionExpr.from( "pathMatch", ValueExpr.string( "myPath" ), ValueExpr.string( "/fisk" ), ValueExpr.number( 3 ) ) );

        assertTrue( pathMatch instanceof BoolQueryBuilder );
    }

    @Test
    public void test_range()
    {
        final QueryBuilder pathMatch = FunctionExpressionBuilder.build(
            FunctionExpr.from( "range", ValueExpr.string( "MyField" ), ValueExpr.instant( "1975-08-01T10:00Z" ),
                               ValueExpr.instant( "1975-08-01T10:00Z" ) ) );

        assertTrue( pathMatch instanceof RangeQueryBuilder );
    }

    @Test
    public void test_stemmed()
    {
        final QueryBuilder stemmed = FunctionExpressionBuilder.build(
            FunctionExpr.from( "stemmed", ValueExpr.string( "field" ), ValueExpr.string( "organize" ), ValueExpr.string( "OR" ),
                               ValueExpr.string( "en" ) ) );
        assertTrue( stemmed instanceof SimpleQueryStringBuilder );
    }

    @Test
    public void test_unsupported()
    {
        assertThrows(UnsupportedOperationException.class, () -> FunctionExpressionBuilder.build(
            FunctionExpr.from( "unsupported", ValueExpr.string( "field" ), ValueExpr.string( "organize" ), ValueExpr.string( "OR" ),
                               ValueExpr.string( "en" ) ) ));
    }
}
