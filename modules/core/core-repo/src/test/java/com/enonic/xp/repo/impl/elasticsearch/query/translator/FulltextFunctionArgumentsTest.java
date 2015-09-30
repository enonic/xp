package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import java.util.List;

import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.function.FulltextFunctionArguments;
import com.enonic.xp.repo.impl.elasticsearch.function.FunctionQueryBuilderException;
import com.enonic.xp.repo.impl.entity.NodeConstants;

public class FulltextFunctionArgumentsTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void fullText3Arguments()
    {
        final List<ValueExpr> arguments =
            Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "and" ) );

        final FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        Assert.assertEquals( "myField", functionArguments.getWeightedQueryFieldName().iterator().next().getBaseFieldName() );
        Assert.assertEquals( "SearchString", functionArguments.getSearchString() );
        Assert.assertEquals( SimpleQueryStringBuilder.Operator.AND, functionArguments.getOperator() );
    }

    @Test
    public void fullText2Arguments()
    {
        final List<ValueExpr> arguments = Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ) );

        final FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        Assert.assertEquals( "myField", functionArguments.getWeightedQueryFieldName().iterator().next().getBaseFieldName() );
        Assert.assertEquals( "SearchString", functionArguments.getSearchString() );
        Assert.assertEquals( SimpleQueryStringBuilder.Operator.OR, functionArguments.getOperator() );
        Assert.assertEquals( NodeConstants.DEFAULT_FULLTEXT_SEARCH_ANALYZER, functionArguments.getAnalyzer() );
    }

    @Test
    public void fullText1Argument()
    {
        this.exception.expect( FunctionQueryBuilderException.class );
        this.exception.expectMessage( "Wrong number of arguments (1) for function 'fulltext' (expected 2 to 4)" );

        final List<ValueExpr> arguments = Lists.newArrayList( ValueExpr.string( "myField" ) );
        new FulltextFunctionArguments( arguments );
    }

    @Test
    public void analyzer()
    {
        final List<ValueExpr> arguments =
            Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "OR" ),
                                ValueExpr.string( "myAnalyzer" ) );

        final FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        Assert.assertEquals( "myField", functionArguments.getWeightedQueryFieldName().iterator().next().getBaseFieldName() );
        Assert.assertEquals( "SearchString", functionArguments.getSearchString() );
        Assert.assertEquals( SimpleQueryStringBuilder.Operator.OR, functionArguments.getOperator() );
        Assert.assertEquals( "myAnalyzer", functionArguments.getAnalyzer() );
    }


    @Test
    public void fullIllegalOperatorArgument()
    {
        this.exception.expect( FunctionQueryBuilderException.class );
        this.exception.expectMessage( "Illegal argument 'DUMMY' in function 'fulltext', positon 3" );

        final List<ValueExpr> arguments =
            Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "dummy" ) );
        new FulltextFunctionArguments( arguments );
    }
}
