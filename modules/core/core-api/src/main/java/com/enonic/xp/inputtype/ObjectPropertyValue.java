package com.enonic.xp.inputtype;

import java.util.Map;

public record ObjectPropertyValue(Map<String, PropertyValue> value)
    implements PropertyValue
{
    @Override
    public Object getRawValue()
    {
        return value;
    }

    @Override
    public boolean isObject()
    {
        return true;
    }

    @Override
    public Map<String, PropertyValue> asObject()
    {
        return value;
    }
}
