package com.enonic.wem.api.query;

public class Value
{
    final Object value;

    public Value( final Object value )
    {
        this.value = value;
    }

    public String getString()
    {
        return value.toString();
    }

    @Override
    public String toString()
    {
        if ( this.value instanceof String )
        {
            return "\"" + this.value + "\"";
        }

        return value.toString();
    }
}
