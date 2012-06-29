package com.enonic.wem.core.content.config.field;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.config.field.type.FieldType;

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

    private Object fieldConfig;

    public FieldType getType()
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

    public Object getFieldConfig()
    {
        return fieldConfig;
    }

    @Override
    ConfigItemJsonGenerator getJsonGenerator()
    {
        return FieldJsonGenerator.DEFAULT;
    }

    @Override
    public String toString()
    {
        FieldPath fieldPath = getPath();
        if ( fieldPath != null )
        {
            return fieldPath.toString();
        }
        else
        {
            return getName() + "?";
        }
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
            Preconditions.checkNotNull( value, "label cannot be null" );

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

        public Builder fieldConfig( Object object )
        {
            field.fieldConfig = object;
            return this;
        }

        public Field build()
        {
            Preconditions.checkNotNull( field.getName(), "name cannot be null" );
            Preconditions.checkNotNull( field.getType(), "type cannot be null" );

            return field;
        }
    }
}
