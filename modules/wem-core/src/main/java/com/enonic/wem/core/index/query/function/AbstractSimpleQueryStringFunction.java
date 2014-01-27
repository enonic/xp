package com.enonic.wem.core.index.query.function;

import java.util.List;

import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.wem.api.query.expr.ValueExpr;

public class AbstractSimpleQueryStringFunction
    extends AbstractFunctionArguments
{
    public static final int FIELDNAME_INDEX = 0;

    public static final int SEARCHSTRING_INDEX = 1;

    public static final int OPERATOR_INDEX = 2;

    private static final int MIN_ARGUMENTS = 2;

    private static final int MAX_ARGUMENTS = 3;

    private final String fieldName;

    private SimpleQueryStringBuilder.Operator operator = SimpleQueryStringBuilder.Operator.OR;

    private final String searchString;

    public AbstractSimpleQueryStringFunction( final List<ValueExpr> arguments )
    {
        verifyNumberOfArguments( arguments );

        fieldName = arguments.get( FIELDNAME_INDEX ).getValue().asString();
        searchString = arguments.get( SEARCHSTRING_INDEX ).getValue().asString();

        setOperator( arguments );
    }


    @Override
    int getMinArguments()
    {
        return MIN_ARGUMENTS;
    }

    @Override
    int getMaxArguments()
    {
        return MAX_ARGUMENTS;
    }

    @Override
    public String getFunctionName()
    {
        return null;
    }

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


    public String getFieldName()
    {
        return fieldName;
    }

    public SimpleQueryStringBuilder.Operator getOperator()
    {
        return operator;
    }

    public String getSearchString()
    {
        return searchString;
    }
}
