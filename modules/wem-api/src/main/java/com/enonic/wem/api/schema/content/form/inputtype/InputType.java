package com.enonic.wem.api.schema.content.form.inputtype;


import com.google.common.base.Objects;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public abstract class InputType
{
    private final String name;

    private final Class configClass;

    private final boolean builtIn;

    protected InputType()
    {
        this.name = resolveName();
        this.builtIn = resolveBuiltIn();
        this.configClass = null;
    }

    protected InputType( final Class configClass )
    {
        this.name = resolveName();
        this.builtIn = resolveBuiltIn();
        this.configClass = configClass;
    }

    public final String getName()
    {
        return name;
    }

    public final boolean isBuiltIn()
    {
        return builtIn;
    }

    public final boolean requiresConfig()
    {
        return configClass != null;
    }

    public final Class requiredConfigClass()
    {
        return configClass;
    }

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return null;
    }

    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return null;
    }

    public abstract void checkValidity( final Property property );

    public abstract void checkBreaksRequiredContract( final Property property );

    public abstract Value newValue( final String value );

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
        if ( !( o instanceof InputType ) )
        {
            return false;
        }

        final InputType that = (InputType) o;

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
        return !( this instanceof InputTypeExtension );
    }
}
