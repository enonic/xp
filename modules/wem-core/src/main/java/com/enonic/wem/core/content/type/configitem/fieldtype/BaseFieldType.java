package com.enonic.wem.core.content.type.configitem.fieldtype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.datatype.DataType;

public abstract class BaseFieldType
    implements FieldType
{
    private String className;

    private String name;

    private DataType dataType;

    BaseFieldType( final String name, final DataType dataType )
    {
        this.name = name;
        this.dataType = dataType;
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

    public DataType getDataType()
    {
        return dataType;
    }

    public abstract boolean validData( Data data );

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
        if ( !dataType.equals( that.dataType ) )
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
        result = 31 * result + dataType.hashCode();
        return result;
    }
}
