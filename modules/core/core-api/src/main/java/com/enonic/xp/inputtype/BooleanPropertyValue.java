package com.enonic.xp.inputtype;

public record BooleanPropertyValue(Boolean value)
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
        return value != null ? value.toString() : "";
    }

    @Override
    public boolean isBoolean()
    {
        return true;
    }

    @Override
    public boolean asBoolean()
    {
        return value;
    }
}
