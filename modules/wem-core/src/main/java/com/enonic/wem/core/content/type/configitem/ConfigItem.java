package com.enonic.wem.core.content.type.configitem;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.field.type.FieldType;
import com.enonic.wem.core.content.type.configitem.field.type.FieldTypeConfig;

/**
 *
 */
public abstract class ConfigItem
{
    private String name;

    private FieldPath path;

    private ConfigItemType itemType;

    protected ConfigItem( final ConfigItemType itemType )
    {
        this.itemType = itemType;
    }

    public ConfigItemType getItemType()
    {
        return itemType;
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

    abstract ConfigItemSerializerJson getJsonGenerator();

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

        private ConfigItemType itemType;

        private String helpText;

        private String validationRegexp;

        private FieldTypeConfig fieldTypeConfig;

        private FieldType fieldType;


        private Builder()
        {

        }

        public Builder itemType( final ConfigItemType value )
        {
            itemType = value;
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
            if ( itemType == ConfigItemType.FIELD )
            {
                return buildField();
            }
            else if ( itemType == ConfigItemType.SUB_TYPE )
            {
                return buildSubType();
            }
            else
            {
                throw new IllegalArgumentException( "Unkown ConfigType: " + itemType );
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
