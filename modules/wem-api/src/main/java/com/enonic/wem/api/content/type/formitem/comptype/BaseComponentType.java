package com.enonic.wem.api.content.type.formitem.comptype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;

public abstract class BaseComponentType
    implements ComponentType
{
    private final String className;

    private final String name;

    private Class configClass;

    BaseComponentType( final String name )
    {
        this.name = name;
        this.className = this.getClass().getName();

    }

    BaseComponentType( final String name, final Class configClass )
    {
        this.name = name;
        this.className = this.getClass().getName();
        this.configClass = configClass;
    }

    @Override
    public final String getName()
    {
        return name;
    }

    @Override
    public final String getClassName()
    {
        return className;
    }

    @Override
    public final boolean requiresConfig()
    {
        return configClass != null;
    }

    @Override
    public final Class requiredConfigClass()
    {
        return configClass;
    }

    public AbstractComponentTypeConfigSerializerJson getComponentTypeConfigJsonGenerator()
    {
        return null;
    }

    @Override
    public AbstractComponentTypeConfigSerializerXml getComponentTypeConfigXmlGenerator()
    {
        return null;
    }

    public abstract void checkValidity( Data data )
        throws InvalidValueTypeException, InvalidValueException;

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
        return name.equals( that.name );
    }

    @Override
    public int hashCode()
    {
        int result = className.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
