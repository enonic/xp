package com.enonic.xp.inputtype;

public record IntegerPropertyValue(Integer value)
    implements PropertyValue
{
    @Override
    public Object getRawValue()
    {
        return value;
    }

    @Override
    public boolean isInteger()
    {
        return true;
    }

    @Override
    public Integer asInteger()
    {
        return value;
    }
}
