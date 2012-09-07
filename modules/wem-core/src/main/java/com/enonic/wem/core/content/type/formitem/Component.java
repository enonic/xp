package com.enonic.wem.core.content.type.formitem;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.data.InvalidDataException;
import com.enonic.wem.core.content.datatype.InvalidValueTypeException;
import com.enonic.wem.core.content.type.formitem.fieldtype.FieldType;
import com.enonic.wem.core.content.type.formitem.fieldtype.FieldTypeConfig;

public class Component
    extends DirectAccessibleFormItem
{
    private FieldType type;

    private String label;

    private boolean immutable;

    private final Occurrences occurrences = new Occurrences( 0, 1 );

    private boolean indexed;

    private String customText;

    private String validationRegexp;

    private String helpText;

    private FieldTypeConfig fieldTypeConfig;

    protected Component()
    {
        super( FormItemType.COMPONENT );
    }

    public FieldType getFieldType()
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

    public String getValidationRegexp()
    {
        return validationRegexp;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public FieldTypeConfig getFieldTypeConfig()
    {
        return fieldTypeConfig;
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

    public void checkValidityAccordingToFieldTypeConfig( final Data data )
        throws InvalidValueException
    {
        if ( fieldTypeConfig != null )
        {
            fieldTypeConfig.checkValidity( data );
        }
    }

    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidDataException, InvalidValueException
    {
        try

        {
            if ( data == null )
            {
                return;
            }

            if ( data != null )
            {
                checkValidityAccordingToFieldTypeConfig( data );
            }
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
        copy.fieldTypeConfig = fieldTypeConfig;
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
        private Component component;

        private Builder()
        {
            component = new Component();
        }

        public Builder name( String value )
        {
            component.setName( value );
            return this;
        }

        public Builder type( FieldType value )
        {
            component.type = value;
            return this;
        }

        public Builder label( String value )
        {
            component.label = value;
            return this;
        }

        public Builder immutable( boolean value )
        {
            component.immutable = value;
            return this;
        }

        public Builder occurrences( Occurrences occurrences )
        {
            component.occurrences.setMinOccurences( occurrences.getMinimum() );
            component.occurrences.setMaxOccurences( occurrences.getMaximum() );
            return this;
        }

        public Builder occurrences( int minOccurrences, int maxOccurrences )
        {
            component.occurrences.setMinOccurences( minOccurrences );
            component.occurrences.setMaxOccurences( maxOccurrences );
            return this;
        }

        public Builder required( boolean value )
        {
            if ( value && !component.occurrences.impliesRequired() )
            {
                component.occurrences.setMinOccurences( 1 );
            }
            else if ( !value && component.occurrences.impliesRequired() )
            {
                component.occurrences.setMinOccurences( 0 );
            }
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                component.occurrences.setMaxOccurences( 0 );
            }
            else
            {
                component.occurrences.setMaxOccurences( 1 );
            }
            return this;
        }

        public Builder indexed( boolean value )
        {
            component.indexed = value;
            return this;
        }

        public Builder customText( String value )
        {
            component.customText = value;
            return this;
        }

        public Builder validationRegexp( String value )
        {
            component.validationRegexp = value;
            return this;
        }

        public Builder helpText( String value )
        {
            component.helpText = value;
            return this;
        }

        public Builder fieldTypeConfig( FieldTypeConfig value )
        {
            component.fieldTypeConfig = value;
            return this;
        }

        public Component build()
        {
            Preconditions.checkNotNull( component.getName(), "name cannot be null" );
            Preconditions.checkNotNull( component.getFieldType(), "type cannot be null" );

            if ( component.getFieldType().requiresConfig() )
            {
                Preconditions.checkArgument( component.getFieldTypeConfig() != null,
                                             "Field [name='%s', type=%s] is missing required FieldTypeConfig: %s", component.getName(),
                                             component.getFieldType().getName(), component.getFieldType().requiredConfigClass().getName() );

                Preconditions.checkArgument( component.getFieldType().requiredConfigClass().isInstance( component.getFieldTypeConfig() ),
                                             "Field [name='%s', type=%s] expects FieldTypeConfig of type [%s] but was: %s",
                                             component.getName(), component.getFieldType().getName(),
                                             component.getFieldType().requiredConfigClass().getName(),
                                             component.getFieldTypeConfig().getClass().getName() );
            }

            component.setPath( new FormItemPath( component.getName() ) );
            return component;
        }
    }
}
