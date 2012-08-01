package com.enonic.wem.core.content.type.configitem.fieldtype;


import com.enonic.wem.core.content.data.Value;
import com.enonic.wem.core.content.type.valuetype.ValueType;

public interface FieldType
{
    String getName();

    String getClassName();

    ValueType getValueType();

    FieldTypeJsonGenerator getJsonGenerator();

    boolean requiresConfig();

    Class requiredConfigClass();

    FieldTypeConfigSerializerJson getFieldTypeConfigJsonGenerator();

    boolean breaksRequiredContract( Value value );
}
