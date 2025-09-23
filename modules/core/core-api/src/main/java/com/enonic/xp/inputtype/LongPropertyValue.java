package com.enonic.xp.inputtype;

public record LongPropertyValue(Long value)
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
        return value != null ? value.toString() : "null";
    }

    @Override
    public boolean isLong()
    {
        return true;
    }

    @Override
    public Long asLong()
    {
        return value;
    }
}
