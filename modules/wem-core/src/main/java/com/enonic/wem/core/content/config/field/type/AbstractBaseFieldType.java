package com.enonic.wem.core.content.config.field.type;


import com.enonic.wem.core.content.FieldValue;
import com.enonic.wem.core.content.config.field.type.value.ValueType;

public abstract class AbstractBaseFieldType
    implements FieldType
{
    private String className;

    private String name;

    private ValueType valueType;

    AbstractBaseFieldType( final String name, final ValueType valueType )
    {
        this.name = name;
        this.valueType = valueType;
        this.className = this.getClass().getName();
    }

    public String getName()
    {
        return name;
    }

    public String getClassName()
    {
        return className;
    }

    public ValueType getValueType()
    {
        return valueType;
    }

    public abstract boolean validValue( FieldValue fieldValue );

}
