package com.enonic.wem.api.content.type.component.inputtype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.component.InvalidValueException;

public abstract class BaseInputType
    implements InputType
{
    private final String className;

    private final String name;

    private Class configClass;

    BaseInputType( final String name )
    {
        this.name = name;
        this.className = this.getClass().getName();

    }

    BaseInputType( final String name, final Class configClass )
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

    public AbstractInputTypeConfigSerializerJson getInputTypeConfigJsonGenerator()
    {
        return null;
    }

    @Override
    public AbstractInputTypeConfigSerializerXml getInputTypeConfigXmlGenerator()
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
        if ( !( o instanceof BaseInputType ) )
        {
            return false;
        }

        final BaseInputType that = (BaseInputType) o;

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
