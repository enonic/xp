package com.enonic.wem.repo.internal.elasticsearch.query.builder.function;

import java.util.List;

import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.xp.core.query.expr.ValueExpr;
import com.enonic.wem.repo.internal.elasticsearch.function.AbstractFunctionArguments;
import com.enonic.wem.repo.internal.elasticsearch.function.FunctionQueryBuilderException;
import com.enonic.wem.repo.internal.elasticsearch.function.WeightedQueryFieldNames;

public abstract class AbstractSimpleQueryStringFunction
    extends AbstractFunctionArguments
{
    private static final int FIELDNAME_INDEX = 0;

    private static final int SEARCHSTRING_INDEX = 1;

    private static final int OPERATOR_INDEX = 2;

    private static final int MIN_ARGUMENTS = 2;

    private static final int MAX_ARGUMENTS = 3;

    private final WeightedQueryFieldNames fieldNames;

    private SimpleQueryStringBuilder.Operator operator = SimpleQueryStringBuilder.Operator.OR;

    private final String searchString;

    protected AbstractSimpleQueryStringFunction( final List<ValueExpr> arguments )
    {
        verifyNumberOfArguments( arguments );

        final String fieldString = arguments.get( FIELDNAME_INDEX ).getValue().asString();

        this.fieldNames = WeightedQueryFieldNames.from( fieldString );

        searchString = arguments.get( SEARCHSTRING_INDEX ).getValue().asString();

        setOperator( arguments );
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

    private void setOperator( final List<ValueExpr> arguments )
    {
        if ( arguments.size() >= OPERATOR_INDEX + 1 && arguments.get( OPERATOR_INDEX ) != null )
        {
            final String operatorAsString = arguments.get( OPERATOR_INDEX ).getValue().asString().toUpperCase();

            try
            {
                final SimpleQueryStringBuilder.Operator operator = SimpleQueryStringBuilder.Operator.valueOf( operatorAsString );
                this.operator = operator;
            }
            catch ( IllegalArgumentException e )
            {
                throw new FunctionQueryBuilderException( "fulltext", OPERATOR_INDEX + 1, operatorAsString, e );
            }
        }
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
