package com.enonic.xp.inputtype;

import java.util.List;

public record ListPropertyValue(List<PropertyValue> value)
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
        return "[" + value + "]";
    }

    @Override
    public boolean isList()
    {
        return true;
    }

    @Override
    public List<PropertyValue> asList()
    {
        return value;
    }
}
