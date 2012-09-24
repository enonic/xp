package com.enonic.wem.api.content.type.formitem;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.formitem.comptype.BaseComponentType;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentType;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypeConfig;

public class Component
    extends HierarchicalFormItem
{
    private BaseComponentType type;

    private String label;

    private boolean immutable;

    private final Occurrences occurrences = new Occurrences( 0, 1 );

    private boolean indexed;

    private String customText;

    private ValidationRegex validationRegexp;

    private String helpText;

    private ComponentTypeConfig componentTypeConfig;

    protected Component()
    {
    }

    public ComponentType getComponentType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }

    public boolean isRequired()
    {
        return occurrences.impliesRequired();
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    public boolean isMultiple()
    {
        return occurrences.isMultiple();
    }

    public Occurrences getOccurrences()
    {
        return occurrences;
    }

    public boolean isIndexed()
    {
        return indexed;
    }

    public String getCustomText()
    {
        return customText;
    }

    public ValidationRegex getValidationRegexp()
    {
        return validationRegexp;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public ComponentTypeConfig getComponentTypeConfig()
    {
        return componentTypeConfig;
    }

    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {
        Preconditions.checkNotNull( data, "Given data is null" );

        if ( isRequired() )
        {
            type.checkBreaksRequiredContract( data );
        }
    }

    public void checkValidityAccordingToComponentTypeConfig( final Data data )
        throws InvalidValueException
    {
        if ( componentTypeConfig != null )
        {
            componentTypeConfig.checkValidity( data );
        }
    }

    public void checkValidationRegexp( final Data data )
        throws InvalidDataException
    {
        try
        {
            validationRegexp.checkValidity( data );
        }
        catch ( BreaksRegexValidationException e )
        {
            throw new InvalidDataException( data, e );
        }
    }

    public void checkValidity( final Data data )
        throws InvalidDataException
    {
        try
        {
            if ( data == null )
            {
                return;
            }

            checkValidityAccordingToComponentTypeConfig( data );
            type.checkValidity( data );
        }
        catch ( InvalidValueException e )
        {
            throw new InvalidDataException( data, e );
        }
        catch ( InvalidValueTypeException e )
        {
            throw new InvalidDataException( data, e );
        }
    }


    @Override
    public Component copy()
    {
        final Component copy = (Component) super.copy();
        copy.type = type;
        copy.label = label;
        copy.immutable = immutable;
        copy.occurrences.setMinOccurences( occurrences.getMinimum() );
        copy.occurrences.setMaxOccurences( occurrences.getMaximum() );
        copy.indexed = indexed;
        copy.customText = customText;
        copy.validationRegexp = validationRegexp;
        copy.helpText = helpText;
        copy.componentTypeConfig = componentTypeConfig;
        return copy;
    }

    public static Builder newComponent()
    {
        return new Builder();
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        private BaseComponentType componentType;

        private String label;

        private boolean immutable;

        private Occurrences occurrences = new Occurrences( 0, 1 );

        private boolean indexed;

        private String customText;

        private ValidationRegex validationRegexp;

        private String helpText;

        private ComponentTypeConfig componentTypeConfig;

        private Builder()
        {
            // protection
        }

        public Builder name( String value )
        {
            name = value;
            return this;
        }

        public Builder type( BaseComponentType value )
        {
            componentType = value;
            return this;
        }

        public Builder label( String value )
        {
            label = value;
            return this;
        }

        public Builder immutable( boolean value )
        {
            immutable = value;
            return this;
        }

        public Builder occurrences( Occurrences value )
        {
            occurrences.setMinOccurences( value.getMinimum() );
            occurrences.setMaxOccurences( value.getMaximum() );
            return this;
        }

        public Builder occurrences( int minOccurrences, int maxOccurrences )
        {
            occurrences.setMinOccurences( minOccurrences );
            occurrences.setMaxOccurences( maxOccurrences );
            return this;
        }

        public Builder required( boolean value )
        {
            if ( value && !occurrences.impliesRequired() )
            {
                occurrences.setMinOccurences( 1 );
            }
            else if ( !value && occurrences.impliesRequired() )
            {
                occurrences.setMinOccurences( 0 );
            }
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                occurrences.setMaxOccurences( 0 );
            }
            else
            {
                occurrences.setMaxOccurences( 1 );
            }
            return this;
        }

        public Builder indexed( boolean value )
        {
            indexed = value;
            return this;
        }

        public Builder customText( String value )
        {
            customText = value;
            return this;
        }

        public Builder validationRegexp( String value )
        {
            validationRegexp = new ValidationRegex( value );
            return this;
        }

        public Builder helpText( String value )
        {
            helpText = value;
            return this;
        }

        public Builder componentTypeConfig( ComponentTypeConfig value )
        {
            componentTypeConfig = value;
            return this;
        }

        public Component build()
        {
            Preconditions.checkNotNull( name, "name cannot be null" );
            Preconditions.checkNotNull( componentType, "componentType cannot be null" );

            if ( componentType.requiresConfig() )
            {
                Preconditions.checkArgument( componentTypeConfig != null,
                                             "Component [name='%s', type=%s] is missing required ComponentTypeConfig: %s", name,
                                             componentType.getName(), componentType.requiredConfigClass().getName() );

                //noinspection ConstantConditions
                Preconditions.checkArgument( componentType.requiredConfigClass().isInstance( componentTypeConfig ),
                                             "Component [name='%s', type=%s] expects ComponentTypeConfig of type [%s] but was: %s", name,
                                             componentType.getName(), componentType.requiredConfigClass().getName(),
                                             componentTypeConfig.getClass().getName() );
            }

            Component component = new Component();
            component.setName( name );
            component.type = componentType;
            component.label = label;
            component.immutable = immutable;
            component.occurrences.setMinOccurences( occurrences.getMinimum() );
            component.occurrences.setMaxOccurences( occurrences.getMaximum() );
            component.indexed = indexed;
            component.customText = customText;
            component.validationRegexp = validationRegexp;
            component.helpText = helpText;
            component.componentTypeConfig = componentTypeConfig;
            component.setPath( new FormItemPath( component.getName() ) );
            return component;
        }
    }
}
