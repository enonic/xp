package com.enonic.xp.form.inputtype;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.Occurrences;

@Beta
public abstract class InputType
{
    private final InputTypeName name;

    private final Class configClass;

    private final boolean requiresConfig;

    protected InputType( final String name, final Class configClass, final boolean requiresConfig )
    {
        this.configClass = configClass;
        this.name = InputTypeName.from( name );
        this.requiresConfig = requiresConfig;
    }

    public String getName()
    {
        return name.toString();
    }

    public final boolean requiresConfig()
    {
        return requiresConfig;
    }

    public final boolean hasConfig()
    {
        return configClass != null;
    }

    public final Class requiredConfigClass()
    {
        return configClass;
    }

    public InputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return null;
    }

    public InputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return null;
    }

    public abstract void checkBreaksRequiredContract( final Property property );

    public abstract void checkTypeValidity( final Property property );

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

        return Objects.equals( this.name, that.name ) && Objects.equals( this.configClass, that.configClass );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.name, this.configClass );
    }

    @Override
    public String toString()
    {
        return this.name.toString();
    }

    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

    public void validateOccurrences( final Occurrences occurrences )
    {
        // Default: nothing
    }

    public abstract Value createPropertyValue( final String value, final InputTypeConfig config );
}
