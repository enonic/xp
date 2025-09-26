package com.enonic.xp.inputtype;

import java.util.Objects;

public record StringPropertyValue(String value)
    implements PropertyValue
{
    public StringPropertyValue( String value )
    {
        this.value = Objects.requireNonNull( value );
    }

    @Override
    public String toString()
    {
        return value;
    }
}
