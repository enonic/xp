package com.enonic.wem.core.content.type.configitem.fieldtype;


import com.enonic.wem.core.content.type.valuetype.ValueType;
import com.enonic.wem.core.content.type.valuetype.ValueTypes;

public class Virtual
    implements FieldType
{
    private String className;

    Virtual()
    {
        this.className = this.getClass().getName();
    }

    public String getName()
    {
        return "virtual";
    }

    public String getClassName()
    {
        return className;
    }

    public ValueType getValueType()
    {
        return ValueTypes.COMPUTED;
    }

    public FieldTypeJsonGenerator getJsonGenerator()
    {
        return BaseFieldTypeJsonGenerator.DEFAULT;
    }

    public boolean requiresConfig()
    {
        return false;
    }

    public Class requiredConfigClass()
    {
        return null;
    }

    public FieldTypeConfigSerializerJson getFieldTypeConfigJsonGenerator()
    {
        return null;
    }
}
