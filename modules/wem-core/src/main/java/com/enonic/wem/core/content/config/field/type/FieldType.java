package com.enonic.wem.core.content.config.field.type;


import com.enonic.wem.core.content.config.field.type.value.ValueType;

public interface FieldType
{
    String getName();

    String getClassName();

    ValueType getValueType();

    FieldTypeJsonGenerator getJsonGenerator();

}
