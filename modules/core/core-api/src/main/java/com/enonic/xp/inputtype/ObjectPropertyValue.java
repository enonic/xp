package com.enonic.xp.inputtype;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record ObjectPropertyValue(Map<String, PropertyValue> value)
    implements PropertyValue
{
    public ObjectPropertyValue( final Map<String, PropertyValue> value )
    {
        this.value = Collections.unmodifiableMap( Objects.requireNonNull( value ) );
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
