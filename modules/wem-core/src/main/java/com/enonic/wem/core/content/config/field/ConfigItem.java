package com.enonic.wem.core.content.config.field;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.config.field.type.FieldType;
import com.enonic.wem.core.content.config.field.type.FieldTypeConfig;

public abstract class ConfigItem
{
    private String name;

    private FieldPath path;

    private ConfigType configType;

    protected ConfigItem( final ConfigType configType )
    {
        this.configType = configType;
    }

    public ConfigType getConfigType()
    {
        return configType;
    }

    void setName( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    void setPath( final FieldPath path )
    {
        this.path = path;
    }

    public FieldPath getPath()
    {
        return path;
    }

    abstract ConfigItemJsonGenerator getJsonGenerator();

    public static ConfigItem.Builder newConfigItemBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        private String label;

        private boolean required;

        private boolean immutable;

        private boolean indexed;

        private Multiple multiple;

        private String customText;

        private ConfigType configType;

        private String helpText;

        private String validationRegexp;

        private FieldTypeConfig fieldTypeConfig;

        private FieldType fieldType;


        private Builder()
        {

        }

        public Builder configType( final ConfigType value )
        {
            configType = value;
            return this;
        }

        public Builder name( String value )
        {
            name = value;
            return this;
        }

        public Builder label( String value )
        {
            label = value;
            return this;
        }

        public Builder required( boolean value )
        {
            required = value;
            return this;
        }

        public Builder immutable( boolean value )
        {
            immutable = value;
            return this;
        }

        public Builder multiple( Multiple multiple )
        {
            this.multiple = multiple;
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                multiple = new Multiple( 0, 0 );
            }
            else
            {
                multiple = null;
            }
            return this;
        }

        public Builder multiple( int minEntries, int maxEntries )
        {
            Preconditions.checkArgument( minEntries >= 0 );
            Preconditions.checkArgument( maxEntries >= 0 );

            multiple = new Multiple( minEntries, maxEntries );
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
            validationRegexp = value;
            return this;
        }

        public Builder helpText( String value )
        {
            helpText = value;
            return this;
        }

        public Builder fieldTypeConfig( FieldTypeConfig value )
        {
            fieldTypeConfig = value;
            return this;
        }

        public Builder fieldType( FieldType value )
        {
            fieldType = value;
            return this;
        }

        public ConfigItem build()
        {
            if ( configType == ConfigType.FIELD )
            {
                return buildField();
            }
            else if ( configType == ConfigType.SUB_TYPE )
            {
                return buildSubType();
            }
            else
            {
                throw new IllegalArgumentException( "Unkown ConfigType: " + configType );
            }
        }

        private SubType buildSubType()
        {
            SubType.Builder builder = SubType.newBuilder();
            builder.name( name );
            builder.label( label );
            builder.required( required );
            builder.immutable( immutable );
            builder.helpText( helpText );
            if ( multiple != null )
            {
                builder.multiple( multiple.getMinimumEntries(), multiple.getMaximumEntries() );
            }
            return builder.build();
        }

        private Field buildField()
        {
            Preconditions.checkNotNull( name, "name cannot be null" );
            Preconditions.checkNotNull( fieldType, "type cannot be null" );

            Field.Builder builder = Field.newBuilder();
            builder.name( name );
            if ( label != null )
            {
                builder.label( label );
            }
            builder.required( required );
            builder.immutable( immutable );
            builder.indexed( indexed );
            builder.helpText( helpText );
            builder.customText( customText );
            if ( multiple != null )
            {
                builder.multiple( multiple.getMinimumEntries(), multiple.getMaximumEntries() );
            }
            builder.type( fieldType );
            builder.fieldTypeConfig( fieldTypeConfig );
            return builder.build();
        }
    }

}
