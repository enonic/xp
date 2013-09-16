package com.enonic.wem.query;

import com.google.common.base.Joiner;

public abstract class DynamicOrder
    extends OrderSpec
{
    private final String name;

    private final Expression[] args;

    public DynamicOrder( final Direction direction, final String name, final Expression... args )
    {
        super( direction );
        this.name = name;
        this.args = args;
    }

    public final String getName()
    {
        return name;
    }

    public final Expression[] getArgs()
    {
        return args;
    }

    @Override
    public final String toString()
    {
        return this.name + "(" + Joiner.on( ", " ).join( this.args ) + ") " + getDirection().toString();
    }
}
