package com.enonic.xp.inputtype;

public record StringPropertyValue(String value)
    implements PropertyValue
{
    @Override
    public Object getRawValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public boolean isString()
    {
        return true;
    }

    @Override
    public String asString()
    {
        return value;
    }
}
