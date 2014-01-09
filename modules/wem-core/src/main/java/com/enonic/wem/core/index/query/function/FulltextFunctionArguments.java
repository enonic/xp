package com.enonic.wem.core.index.query.function;

import java.util.List;

import org.elasticsearch.index.query.QueryStringQueryBuilder;

import com.enonic.wem.api.query.expr.ValueExpr;

public class FulltextFunctionArguments
    extends AbstractFunctionArguments
{
    public static final int FIELDNAME_INDEX = 0;

    public static final int SEARCHSTRING_INDEX = 1;

    public static final int OPERATOR_INDEX = 2;

    private int minArguments = 2;

    private int maxArguments = 3;

    private final String functionName = "fulltext";

    private String fieldName;

    private QueryStringQueryBuilder.Operator operator = QueryStringQueryBuilder.Operator.OR;

    private final String searchString;

    public FulltextFunctionArguments( final List<ValueExpr> arguments )
    {
        verifyNumberOfArguments( arguments );

        fieldName = arguments.get( FIELDNAME_INDEX ).getValue().asString();
        searchString = arguments.get( SEARCHSTRING_INDEX ).getValue().asString();

        setOperator( arguments );
    }

    private void setOperator( final List<ValueExpr> arguments )
    {
        if ( arguments.size() >= OPERATOR_INDEX + 1 && arguments.get( OPERATOR_INDEX ) != null )
        {
            final String operatorAsString = arguments.get( OPERATOR_INDEX ).getValue().asString().toUpperCase();

            try
            {
                final QueryStringQueryBuilder.Operator operator = QueryStringQueryBuilder.Operator.valueOf( operatorAsString );
                this.operator = operator;
            }
            catch ( IllegalArgumentException e )
            {
                throw new FunctionQueryBuilderException( "fulltext", OPERATOR_INDEX + 1, operatorAsString, e );
            }
        }
    }

    public QueryStringQueryBuilder.Operator getOperator()
    {
        return operator;
    }

    public String getSearchString()
    {
        return searchString;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    int getMinArguments()
    {
        return minArguments;
    }

    int getMaxArguments()
    {
        return maxArguments;
    }

    public String getFunctionName()
    {
        return functionName;
    }
}
