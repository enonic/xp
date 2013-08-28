package com.enonic.wem.api.query;

class ValueImpl
    implements Value
{

    final Object value;

    public ValueImpl( final Object value )
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
        return value.toString();
    }
}
