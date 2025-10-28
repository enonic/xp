package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.node.NodeConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FulltextFunctionArgumentsTest
{
    @Test
    void fullText3Arguments()
    {
        final List<ValueExpr> arguments =
            List.of( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "and" ) );

        final FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        assertEquals( "myField", functionArguments.getWeightedQueryFieldName().getWeightedQueryFieldNames().iterator().next().getBaseFieldName() );
        assertEquals( "SearchString", functionArguments.getSearchString() );
        assertEquals( SimpleQueryStringBuilder.Operator.AND, functionArguments.getOperator() );
    }

    @Test
    void fullText2Arguments()
    {
        final List<ValueExpr> arguments = List.of( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ) );

        final FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        assertEquals( "myField", functionArguments.getWeightedQueryFieldName().getWeightedQueryFieldNames().iterator().next().getBaseFieldName() );
        assertEquals( "SearchString", functionArguments.getSearchString() );
        assertEquals( SimpleQueryStringBuilder.Operator.OR, functionArguments.getOperator() );
        assertEquals( NodeConstants.DEFAULT_FULLTEXT_SEARCH_ANALYZER, functionArguments.getAnalyzer() );
    }

    @Test
    void fullText1Argument()
    {
        final List<ValueExpr> arguments = List.of( ValueExpr.string( "myField" ) );

        final FunctionQueryBuilderException ex = assertThrows(FunctionQueryBuilderException.class, () -> {
            new FulltextFunctionArguments( arguments );
        });
        assertEquals( "Wrong number of arguments (1) for function 'fulltext' (expected 2 to 4)", ex.getMessage());
    }

    @Test
    void analyzer()
    {
        final List<ValueExpr> arguments =
            List.of( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "OR" ),
                     ValueExpr.string( "myAnalyzer" ) );

        final FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        assertEquals( "myField", functionArguments.getWeightedQueryFieldName().getWeightedQueryFieldNames().iterator().next().getBaseFieldName() );
        assertEquals( "SearchString", functionArguments.getSearchString() );
        assertEquals( SimpleQueryStringBuilder.Operator.OR, functionArguments.getOperator() );
        assertEquals( "myAnalyzer", functionArguments.getAnalyzer() );
    }


    @Test
    void fullIllegalOperatorArgument()
    {
        final List<ValueExpr> arguments =
            List.of( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "dummy" ) );

        final FunctionQueryBuilderException ex = assertThrows(FunctionQueryBuilderException.class, () -> {
            new FulltextFunctionArguments( arguments );
        });
        assertEquals( "Illegal argument 'DUMMY' in function 'fulltext', position 3", ex.getMessage());
    }
}
