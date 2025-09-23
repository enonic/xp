package com.enonic.xp.inputtype;

public record DoublePropertyValue(Double value)
    implements PropertyValue
{
    @Override
    public Object getRawValue()
    {
        return value;
    }

    @Override
    public boolean isDouble()
    {
        return true;
    }

    @Override
    public Double asDouble()
    {
        return value;
    }
}
