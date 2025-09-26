package com.enonic.xp.inputtype;

public record DoublePropertyValue(double value)
    implements PropertyValue
{
    @Override
    public String toString()
    {
        return Double.toString( value );
    }
}
