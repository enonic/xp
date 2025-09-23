package com.enonic.xp.inputtype;

import java.util.List;
import java.util.Map;

public sealed interface PropertyValue
    permits StringPropertyValue, BooleanPropertyValue, ListPropertyValue, ObjectPropertyValue, IntegerPropertyValue, LongPropertyValue,
    DoublePropertyValue
{
    Object getRawValue();

    default boolean isString()
    {
        return false;
    }

    default String asString()
    {
        throw new UnsupportedOperationException();
    }

    default boolean isBoolean()
    {
        return false;
    }

    default boolean asBoolean()
    {
        throw new UnsupportedOperationException();
    }

    default boolean isList()
    {
        return false;
    }

    default List<PropertyValue> asList()
    {
        throw new UnsupportedOperationException();
    }

    default boolean isObject()
    {
        return false;
    }

    default Map<String, PropertyValue> asObject()
    {
        throw new UnsupportedOperationException();
    }

    default boolean isInteger()
    {
        return false;
    }

    default Integer asInteger()
    {
        throw new UnsupportedOperationException();
    }

    default boolean isLong()
    {
        return false;
    }

    default Long asLong()
    {
        throw new UnsupportedOperationException();
    }

    default boolean isDouble()
    {
        return false;
    }

    default Double asDouble()
    {
        throw new UnsupportedOperationException();
    }

}
