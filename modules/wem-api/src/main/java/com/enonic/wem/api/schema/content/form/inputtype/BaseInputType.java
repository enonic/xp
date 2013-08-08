package com.enonic.wem.api.schema.content.form.inputtype;


import com.google.common.base.Objects;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;
import com.enonic.wem.api.schema.content.form.inputtype.config.AbstractInputTypeConfigJsonGenerator;

public abstract class BaseInputType
    implements InputType
{
    private final String name;

    private Class configClass;

    private boolean builtIn;

    BaseInputType()
    {
        this.name = resolveName();
        this.builtIn = resolveBuiltIn();
    }

    BaseInputType( final Class configClass )
    {
        this.name = resolveName();
        this.builtIn = resolveBuiltIn();
        this.configClass = configClass;
    }

    @Override
    public final String getName()
    {
        return name;
    }

    @Override
    public boolean isBuiltIn()
    {
        return builtIn;
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

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return null;
    }

    @Override
    public AbstractInputTypeConfigJsonGenerator getInputTypeConfigJsonGenerator()
    {
        return null;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlGenerator()
    {
        return null;
    }

    public abstract void checkValidity( Property property )
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

        return Objects.equal( this.getClass(), that.getClass() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.getClass() );
    }

    private String resolveName()
    {
        return this.getClass().getSimpleName();
    }

    private boolean resolveBuiltIn()
    {
        return this.getClass().getPackage().equals( BaseInputType.class.getPackage() );
    }
}
