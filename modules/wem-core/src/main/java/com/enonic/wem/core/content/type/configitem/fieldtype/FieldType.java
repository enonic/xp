package com.enonic.wem.core.content.type.configitem.fieldtype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.datatype.DataType;

/**
 * Common interface for all kinds of field types.
 */
public interface FieldType
{
    String getName();

    String getClassName();

    DataType getDataType();

    FieldTypeJsonGenerator getJsonGenerator();

    boolean requiresConfig();

    Class requiredConfigClass();

    FieldTypeConfigSerializerJson getFieldTypeConfigJsonGenerator();

    boolean breaksRequiredContract( Data data );
}
