package com.enonic.xp.inputtype;

public record IntegerPropertyValue(int value)
    implements PropertyValue
{
    @Override
    public String toString()
    {
        return Integer.toString( value );
    }
}
