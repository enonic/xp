package com.enonic.wem.api.query;

class LiteralImpl
    implements Literal
{
    private final Value value;

    public LiteralImpl( final Value value )
    {
        this.value = value;
    }

    public LiteralImpl( final String value )
    {
        this.value = new ValueImpl( value );
    }

    @Override
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
