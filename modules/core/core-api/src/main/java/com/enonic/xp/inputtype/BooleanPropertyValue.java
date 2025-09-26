package com.enonic.xp.inputtype;

public record BooleanPropertyValue(boolean value)
    implements PropertyValue
{
    @Override
    public String toString()
    {
        return Boolean.toString( value );
    }
}
