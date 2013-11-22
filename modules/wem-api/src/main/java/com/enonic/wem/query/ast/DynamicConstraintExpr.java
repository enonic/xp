package com.enonic.wem.query.ast;

public final class DynamicConstraintExpr
    implements ConstraintExpr
{
    private final FunctionExpr function;

    public DynamicConstraintExpr( final FunctionExpr function )
    {
        this.function = function;
    }

    public FunctionExpr getFunction()
    {
        return this.function;
    }

    @Override
    public String toString()
    {
        return this.function.toString();
    }
}
