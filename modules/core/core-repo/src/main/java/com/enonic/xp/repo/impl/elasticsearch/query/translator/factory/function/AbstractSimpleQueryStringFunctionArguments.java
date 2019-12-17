package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import org.elasticsearch.index.query.Operator;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.expr.ValueExpr;

public abstract class AbstractSimpleQueryStringFunctionArguments
    extends AbstractFunctionArguments
{
    private static final int FIELDNAME_INDEX = 0;

    private static final int SEARCHSTRING_INDEX = 1;

    private static final int OPERATOR_INDEX = 2;

    private static final int ANALYZER_INDEX = 3;

    private static final int MIN_ARGUMENTS = 2;

    private static final int MAX_ARGUMENTS = 4;

    private final WeightedQueryFieldNames fieldNames;

    private Operator operator = Operator.OR;

    private final String searchString;

    private String analyzer;

    AbstractSimpleQueryStringFunctionArguments( final List<ValueExpr> arguments )
    {
        verifyNumberOfArguments( arguments );

        final String fieldString = arguments.get( FIELDNAME_INDEX ).getValue().asString();

        final String normalizedFieldString = fieldString.replace( PropertyPath.ELEMENT_DIVIDER, IndexPath.DIVIDER );

        this.fieldNames = WeightedQueryFieldNames.from( normalizedFieldString );

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
    protected abstract String getFunctionName();

    private void setOperator( final ValueExpr expr )
    {
        if ( expr == null )
        {
            return;
        }

        final String operatorAsString = expr.getValue().asString().toUpperCase();

        try
        {
            this.operator = Operator.valueOf( operatorAsString );
        }
        catch ( IllegalArgumentException e )
        {
            throw new FunctionQueryBuilderException( getFunctionName(), OPERATOR_INDEX + 1, operatorAsString, e );
        }
    }

    public String getAnalyzer()
    {
        return analyzer;
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

    public Operator getOperator()
    {
        return operator;
    }

    public String getSearchString()
    {
        return searchString;
    }

    private void setAnalyzer( final ValueExpr expr )
    {
        this.analyzer = this.resolveAnalyzer( expr != null ? expr.getValue().asString() : "" );
    }

    protected abstract String resolveAnalyzer( final String value );

    public abstract String resolveQueryFieldName( final String baseFieldName );
}
