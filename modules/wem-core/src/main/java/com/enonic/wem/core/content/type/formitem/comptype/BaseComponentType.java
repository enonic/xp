package com.enonic.wem.core.content.type.formitem.comptype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataType;

public abstract class BaseComponentType
    implements ComponentType
{
    private String className;

    private String name;

    private DataType dataType;

    BaseComponentType( final String name, final DataType dataType )
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

    public AbstractComponentTypeConfigSerializerJson getComponentTypeConfigJsonGenerator()
    {
        return null;
    }

    @Override
    public void ensureType( final Data data )
    {
        if ( data.getDataType().equals( dataType ) )
        {
            return;
        }
        dataType.ensureType( data );
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
        if ( !( o instanceof BaseComponentType ) )
        {
            return false;
        }

        final BaseComponentType that = (BaseComponentType) o;

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
