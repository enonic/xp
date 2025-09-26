package com.enonic.xp.inputtype;

public record LongPropertyValue(long value)
    implements PropertyValue
{
    @Override
    public String toString()
    {
        return Long.toString( value );
    }
}
