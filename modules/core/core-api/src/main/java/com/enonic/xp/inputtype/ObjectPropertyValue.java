package com.enonic.xp.inputtype;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

public record ObjectPropertyValue(Map<String, PropertyValue> value)
    implements PropertyValue
{
    public ObjectPropertyValue( final Map<String, PropertyValue> value )
    {
        this.value = ImmutableMap.copyOf( value );
    }

    @Override
    public Set<Map.Entry<String, PropertyValue>> getProperties()
    {
        return value.entrySet();
    }

    @Override
    public String toString()
    {
        return value.toString();
    }
}
