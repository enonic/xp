package com.enonic.wem.query;

import com.google.common.base.Preconditions;

public final class FieldExpr
    implements Expression
{
    private final String name;

    public FieldExpr( final String name )
    {
        Preconditions.checkNotNull( name, "Name cannot be null" );
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
