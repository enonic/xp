package com.enonic.wem.core.content.config.field.type;


import com.enonic.wem.core.content.config.field.type.value.ValueType;
import com.enonic.wem.core.content.config.field.type.value.ValueTypes;

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
}
