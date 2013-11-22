package com.enonic.wem.query.ast;

public final class FieldExpr
    implements Expression
{
    public final String name;

    public FieldExpr( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
