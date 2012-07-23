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

    public FieldTypeConfigJsonGenerator getFieldTypeConfigJsonGenerator()
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
        if ( !( o instanceof AbstractBaseFieldType ) )
        {
            return false;
        }

        final AbstractBaseFieldType that = (AbstractBaseFieldType) o;

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
