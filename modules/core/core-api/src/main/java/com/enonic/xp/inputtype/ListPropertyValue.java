package com.enonic.xp.inputtype;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record ListPropertyValue(List<PropertyValue> value)
    implements PropertyValue
{
    public ListPropertyValue( final List<PropertyValue> value )
    {
        this.value = Collections.unmodifiableList( Objects.requireNonNull( value ) );
    }

    @Override
    public String toString()
    {
        return value.toString();
    }
}
