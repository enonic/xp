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

    private boolean required;

    private boolean immutable;

    /**
     * If set, the field is multiple. Otherwise it is not.
     */
    private Multiple multiple;

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
        return required;
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    boolean isMultiple()
    {
        return multiple != null;
    }

    public Multiple getMultiple()
    {
        return multiple;
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
        Preconditions.checkArgument( value.getField() != null, "Given value have no field" );
        Preconditions.checkArgument( value.getField().equals( this ), "Given value's field is not this" );

        if ( !required )
        {
            return false;
        }

        return type.breaksRequiredContract( value );
    }

    @Override
    public String toString()
    {
        ConfigItemPath configItemPath = getPath();
        if ( configItemPath != null )
        {
            return configItemPath.toString();
        }
        else
        {
            return getName() + "?";
        }
    }

    @Override
    public Field copy()
    {
        final Field copy = (Field) super.copy();
        copy.type = type;
        copy.label = label;
        copy.required = required;
        copy.immutable = immutable;
        copy.multiple = multiple;
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

        public Builder required( boolean value )
        {
            field.required = value;
            return this;
        }

        public Builder immutable( boolean value )
        {
            field.immutable = value;
            return this;
        }

        public Builder multiple( Multiple multiple )
        {
            this.field.multiple = multiple;
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                field.multiple = new Multiple( 0, 0 );
            }
            else
            {
                field.multiple = null;
            }
            return this;
        }

        public Builder multiple( int minEntries, int maxEntries )
        {
            Preconditions.checkArgument( minEntries >= 0 );
            Preconditions.checkArgument( maxEntries >= 0 );

            field.multiple = new Multiple( minEntries, maxEntries );
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
