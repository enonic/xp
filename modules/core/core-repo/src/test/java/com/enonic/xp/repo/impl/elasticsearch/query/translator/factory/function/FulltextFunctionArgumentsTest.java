package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import org.elasticsearch.index.query.Operator;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.node.NodeConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FulltextFunctionArgumentsTest
{
    @Test
    public void fullText3Arguments()
    {
        final List<ValueExpr> arguments =
            List.of( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "and" ) );

        final FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        assertEquals( "myField", functionArguments.getWeightedQueryFieldName().iterator().next().getBaseFieldName() );
        assertEquals( "SearchString", functionArguments.getSearchString() );
        assertEquals( Operator.AND, functionArguments.getOperator() );
    }

    @Test
    public void fullText2Arguments()
    {
        final List<ValueExpr> arguments = List.of( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ) );

        final FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        assertEquals( "myField", functionArguments.getWeightedQueryFieldName().iterator().next().getBaseFieldName() );
        assertEquals( "SearchString", functionArguments.getSearchString() );
        assertEquals( Operator.OR, functionArguments.getOperator() );
        assertEquals( NodeConstants.DEFAULT_FULLTEXT_SEARCH_ANALYZER, functionArguments.getAnalyzer() );
    }

    @Test
    public void fullText1Argument()
    {
        final List<ValueExpr> arguments = List.of( ValueExpr.string( "myField" ) );

        final FunctionQueryBuilderException ex = assertThrows(FunctionQueryBuilderException.class, () -> {
            new FulltextFunctionArguments( arguments );
        });
        assertEquals( "Wrong number of arguments (1) for function 'fulltext' (expected 2 to 4)", ex.getMessage());
    }

    @Test
    public void analyzer()
    {
        final List<ValueExpr> arguments =
            List.of( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "OR" ),
                     ValueExpr.string( "myAnalyzer" ) );

        final FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        assertEquals( "myField", functionArguments.getWeightedQueryFieldName().iterator().next().getBaseFieldName() );
        assertEquals( "SearchString", functionArguments.getSearchString() );
        assertEquals( Operator.OR, functionArguments.getOperator() );
        assertEquals( "myAnalyzer", functionArguments.getAnalyzer() );
    }


    @Test
    public void fullIllegalOperatorArgument()
    {
        final List<ValueExpr> arguments =
            List.of( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "dummy" ) );

        final FunctionQueryBuilderException ex = assertThrows(FunctionQueryBuilderException.class, () -> {
            new FulltextFunctionArguments( arguments );
        });
        assertEquals( "Illegal argument 'DUMMY' in function 'fulltext', position 3", ex.getMessage());
    }
}
