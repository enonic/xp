package com.enonic.wem.query;

public class Literal
    implements StaticOperand
{
    private final Value value;

    public Literal( final Value value )
    {
        this.value = value;
    }

    public Literal( final String value )
    {
        this.value = new Value( value );
    }

    public Value getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return this.value.toString();
    }
}
