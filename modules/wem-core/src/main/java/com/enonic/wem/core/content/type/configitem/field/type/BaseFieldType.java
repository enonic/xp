package com.enonic.wem.core.content.type.configitem.field.type;


import com.enonic.wem.core.content.data.Value;
import com.enonic.wem.core.content.type.valuetype.ValueType;

public abstract class BaseFieldType
    implements FieldType
{
    private String className;

    private String name;

    private ValueType valueType;

    BaseFieldType( final String name, final ValueType valueType )
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

    public abstract boolean validValue( Value fieldValue );

    public FieldTypeConfigSerializerJson getFieldTypeConfigJsonGenerator()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof BaseFieldType ) )
        {
            return false;
        }

        final BaseFieldType that = (BaseFieldType) o;

        if ( !className.equals( that.className ) )
        {
            return false;
        }
        if ( !name.equals( that.name ) )
        {
            return false;
        }
        if ( !valueType.equals( that.valueType ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = className.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + valueType.hashCode();
        return result;
    }
}
