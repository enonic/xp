package com.enonic.wem.core.index.query.function;

import java.util.List;

import com.enonic.wem.query.expr.ValueExpr;

public abstract class AbstractFunctionArguments
{
    abstract int getMinArguments();

    abstract int getMaxArguments();

    abstract String getFunctionName();

    protected void verifyNumberOfArguments( final List<ValueExpr> arguments )
    {
        if ( arguments == null || arguments.size() < getMinArguments() || arguments.size() > getMaxArguments() )
        {
            throw new FunctionQueryBuilderException( "Wrong number of arguments (" + ( arguments == null ? "0" : arguments.size() ) +
                                                         ") for function '" + getFunctionName() + "' (expected " + getMinArguments() +
                                                         " to " +
                                                         getMaxArguments() + ")" );
        }
    }
}
