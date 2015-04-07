package com.enonic.xp.form.inputtype;


import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.Occurrences;

@Beta
public abstract class InputType
{
    private final InputTypeName inputTypeName;

    private final Class configClass;

    private final boolean builtIn;

    protected InputType()
    {
        this.builtIn = resolveBuiltIn();
        this.configClass = null;
        final String name = resolveName();
        this.inputTypeName = new InputTypeName( name, !this.builtIn );
    }

    protected InputType( final Class configClass )
    {
        this.builtIn = resolveBuiltIn();
        this.configClass = configClass;
        final String name = resolveName();
        this.inputTypeName = new InputTypeName( name, !this.builtIn );
    }

    public String getName()
    {
        return inputTypeName.toString();
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

    public abstract void checkBreaksRequiredContract( final Property property );

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

        return Objects.equals( this.inputTypeName, that.inputTypeName ) &&
            Objects.equals( this.configClass, that.configClass ) &&
            Objects.equals( this.builtIn, that.builtIn );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.inputTypeName, this.configClass, this.builtIn );
    }

    @Override
    public String toString()
    {
        return this.inputTypeName.toString();
    }

    // TODO: Remove when doing task?: CMS-2256 Validate occurrences before save when user clicks Publish
    public abstract Value newValue( final String value );

    private String resolveName()
    {
        return this.getClass().getSimpleName();
    }

    private boolean resolveBuiltIn()
    {
        return !( this instanceof InputTypeExtension );
    }

    public abstract InputTypeConfig getDefaultConfig();

    /**
     * Override this method to validate what type of Occurrences is allowed for this type.
     */
    public void validateOccurrences( final Occurrences occurrences )
    {
        // Default: nothing
    }
}
