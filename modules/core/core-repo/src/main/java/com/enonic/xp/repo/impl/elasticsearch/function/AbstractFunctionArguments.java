package com.enonic.xp.repo.impl.elasticsearch.function;

import java.util.List;

import com.enonic.xp.query.expr.ValueExpr;

abstract class AbstractFunctionArguments
{
    protected abstract int getMinArguments();

    protected abstract int getMaxArguments();

    protected abstract String getFunctionName();

    void verifyNumberOfArguments( final List<ValueExpr> arguments )
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
