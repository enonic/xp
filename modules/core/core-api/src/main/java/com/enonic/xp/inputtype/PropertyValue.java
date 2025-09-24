package com.enonic.xp.inputtype;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public sealed interface PropertyValue
    permits StringPropertyValue, BooleanPropertyValue, ListPropertyValue, ObjectPropertyValue, IntegerPropertyValue, LongPropertyValue,
    DoublePropertyValue
{
    default Set<Map.Entry<String, PropertyValue>> getProperties()
    {
        return Collections.emptySet();
    }
}
