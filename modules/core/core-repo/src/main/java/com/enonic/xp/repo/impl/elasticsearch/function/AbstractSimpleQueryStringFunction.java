package com.enonic.xp.repo.impl.elasticsearch.function;

import java.util.List;

import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.xp.query.expr.ValueExpr;

public abstract class AbstractSimpleQueryStringFunction
    extends AbstractFunctionArguments
{
    private static final int FIELDNAME_INDEX = 0;

    private static final int SEARCHSTRING_INDEX = 1;

    private static final int OPERATOR_INDEX = 2;

    private static final int ANALYZER_INDEX = 3;

    private static final int MIN_ARGUMENTS = 2;

    private static final int MAX_ARGUMENTS = 4;

    private final WeightedQueryFieldNames fieldNames;

    private SimpleQueryStringBuilder.Operator operator = SimpleQueryStringBuilder.Operator.OR;

    private final String searchString;

    private String analyzer;

    protected abstract String getDefaultAnalyzer();

    public String getAnalyzer()
    {
        return analyzer;
    }

    AbstractSimpleQueryStringFunction( final List<ValueExpr> arguments )
    {
        verifyNumberOfArguments( arguments );

        final String fieldString = arguments.get( FIELDNAME_INDEX ).getValue().asString();

        this.fieldNames = WeightedQueryFieldNames.from( fieldString );

        searchString = arguments.get( SEARCHSTRING_INDEX ).getValue().asString();

        setOperator( getValueAt( arguments, OPERATOR_INDEX ) );
        setAnalyzer( getValueAt( arguments, ANALYZER_INDEX ) );
    }

    @Override
    protected int getMinArguments()
    {
        return MIN_ARGUMENTS;
    }

    @Override
    protected int getMaxArguments()
    {
        return MAX_ARGUMENTS;
    }

    @Override
    public abstract String getFunctionName();

    private void setOperator( final ValueExpr expr )
    {
        if ( expr == null )
        {
            return;
        }

        final String operatorAsString = expr.getValue().asString().toUpperCase();

        try
        {
            this.operator = SimpleQueryStringBuilder.Operator.valueOf( operatorAsString );
        }
        catch ( IllegalArgumentException e )
        {
            throw new FunctionQueryBuilderException( "fulltext", OPERATOR_INDEX + 1, operatorAsString, e );
        }
    }

    private void setAnalyzer( final ValueExpr expr )
    {
        if ( expr == null )
        {
            this.analyzer = getDefaultAnalyzer();
            return;
        }

        this.analyzer = expr.getValue().asString();
    }

    private ValueExpr getValueAt( final List<ValueExpr> arguments, final int pos )
    {
        if ( arguments.size() >= pos + 1 && arguments.get( pos ) != null )
        {
            return arguments.get( pos );
        }

        return null;
    }

    public WeightedQueryFieldNames getWeightedQueryFieldName()
    {
        return fieldNames;
    }

    public SimpleQueryStringBuilder.Operator getOperator()
    {
        return operator;
    }

    public String getSearchString()
    {
        return searchString;
    }

    public abstract String resolveQueryFieldName( final String baseFieldName );
}
