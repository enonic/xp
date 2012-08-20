package com.enonic.wem.core.content.type.configitem;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.data.Value;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldType;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypeConfig;

public class Field
    extends ConfigItem
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

    protected Field()
    {
        super( ConfigItemType.FIELD );
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

    public boolean breaksRequiredContract( final Value value )
    {
        Preconditions.checkNotNull( value, "Given value is null" );
        Preconditions.checkArgument( value.getField() != null, "Given value have no field" );
        Preconditions.checkArgument( value.getField().equals( this ), "Given value's field is not this" );

        if ( !isRequired() )
        {
            return false;
        }

        return type.breaksRequiredContract( value );
    }

    public boolean isValidAccordingToFieldTypeConfig( final Value value )
    {
        return fieldTypeConfig == null || fieldTypeConfig.isValid( value );
    }

    @Override
    public Field copy()
    {
        final Field copy = (Field) super.copy();
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

    public Field copy( final String name )
    {
        final Field field = copy();
        field.setName( name );
        return field;
    }

    public static Builder newField()
    {
        return new Builder();
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Field field;

        private Builder()
        {
            field = new Field();
        }

        public Builder name( String value )
        {
            field.setName( value );
            return this;
        }

        public Builder type( FieldType value )
        {
            field.type = value;
            return this;
        }

        public Builder label( String value )
        {
            field.label = value;
            return this;
        }

        public Builder immutable( boolean value )
        {
            field.immutable = value;
            return this;
        }

        public Builder occurrences( Occurrences occurrences )
        {
            field.occurrences.setMinOccurences( occurrences.getMinimum() );
            field.occurrences.setMaxOccurences( occurrences.getMaximum() );
            return this;
        }

        public Builder occurrences( int minOccurrences, int maxOccurrences )
        {
            field.occurrences.setMinOccurences( minOccurrences );
            field.occurrences.setMaxOccurences( maxOccurrences );
            return this;
        }

        public Builder required( boolean value )
        {
            if ( value && !field.occurrences.impliesRequired() )
            {
                field.occurrences.setMinOccurences( 1 );
            }
            else if ( !value && field.occurrences.impliesRequired() )
            {
                field.occurrences.setMinOccurences( 0 );
            }
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                field.occurrences.setMaxOccurences( 0 );
            }
            else
            {
                field.occurrences.setMaxOccurences( 1 );
            }
            return this;
        }

        public Builder indexed( boolean value )
        {
            field.indexed = value;
            return this;
        }

        public Builder customText( String value )
        {
            field.customText = value;
            return this;
        }

        public Builder validationRegexp( String value )
        {
            field.validationRegexp = value;
            return this;
        }

        public Builder helpText( String value )
        {
            field.helpText = value;
            return this;
        }

        public Builder fieldTypeConfig( FieldTypeConfig value )
        {
            field.fieldTypeConfig = value;
            return this;
        }

        public Field build()
        {
            Preconditions.checkNotNull( field.getName(), "name cannot be null" );
            Preconditions.checkNotNull( field.getFieldType(), "type cannot be null" );

            if ( field.getFieldType().requiresConfig() )
            {
                Preconditions.checkArgument( field.getFieldTypeConfig() != null,
                                             "Field [name='%s', type=%s] is missing required FieldTypeConfig: %s", field.getName(),
                                             field.getFieldType().getName(), field.getFieldType().requiredConfigClass().getName() );

                Preconditions.checkArgument( field.getFieldType().requiredConfigClass().isInstance( field.getFieldTypeConfig() ),
                                             "Field [name='%s', type=%s] expects FieldTypeConfig of type [%s] but was: %s", field.getName(),
                                             field.getFieldType().getName(), field.getFieldType().requiredConfigClass().getName(),
                                             field.getFieldTypeConfig().getClass().getName() );
            }

            field.setPath( new ConfigItemPath( field.getName() ) );
            return field;
        }
    }
}
