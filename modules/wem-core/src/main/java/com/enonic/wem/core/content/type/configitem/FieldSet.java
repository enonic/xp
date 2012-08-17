package com.enonic.wem.core.content.type.configitem;


import com.google.common.base.Preconditions;

public class FieldSet
    extends ConfigItem
{
    private FieldSetType type;

    private String label;

    private ConfigItems configItems = new ConfigItems();

    private boolean required;

    private boolean immutable;

    private Multiple multiple;

    private String customText;

    private String helpText;

    protected FieldSet()
    {
        super( ConfigItemType.FIELD_SET );
    }

    @Override
    void setPath( final ConfigItemPath configItemPath )
    {
        super.setPath( configItemPath );
        configItems.setPath( configItemPath );
    }

    public void addConfigItem( final ConfigItem configItem )
    {
        this.configItems.addConfigItem( configItem );
    }

    public void addField( final Field field )
    {
        Preconditions.checkState( getPath() != null, "Cannot add Field before this FieldSet is added" );

        field.setPath( new ConfigItemPath( getPath(), field.getName() ) );
        this.configItems.addConfigItem( field );
    }

    public void addFieldSet( final FieldSet fieldSet )
    {
        Preconditions.checkState( getPath() != null, "Cannot add FieldSet before this FieldSet is added" );

        fieldSet.setPath( new ConfigItemPath( getPath(), fieldSet.getName() ) );
        this.configItems.addConfigItem( fieldSet );
    }

    public FieldSetType getType()
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

    public boolean isMultiple()
    {
        return multiple != null;
    }

    public Multiple getMultiple()
    {
        return multiple;
    }

    public String getCustomText()
    {
        return customText;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public ConfigItems getConfigItems()
    {
        return configItems;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        ConfigItemPath configItemPath = getPath();
        if ( configItemPath != null )
        {
            s.append( configItemPath.toString() );
        }
        else
        {
            s.append( getName() ).append( "?" );
        }
        if ( isMultiple() )
        {
            s.append( "[]" );
        }

        return s.toString();
    }

    @Override
    public FieldSet copy()
    {
        FieldSet fieldSet = (FieldSet) super.copy();
        fieldSet.type = type;
        fieldSet.label = label;
        fieldSet.required = required;
        fieldSet.immutable = immutable;
        fieldSet.multiple = multiple;
        fieldSet.customText = customText;
        fieldSet.helpText = helpText;
        fieldSet.configItems = configItems.copy();
        return fieldSet;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static Builder newFieldSet()
    {
        return new Builder();
    }

    public ConfigItem getConfig( final ConfigItemPath configItemPath )
    {
        return configItems.getConfigItem( configItemPath );
    }

    public static class Builder
    {
        private FieldSet fieldSet;

        private Builder()
        {
            fieldSet = new FieldSet();
        }

        public Builder typeGroup()
        {
            fieldSet.type = FieldSetType.GROUP;
            return this;
        }

        public Builder typeVisual()
        {
            fieldSet.type = FieldSetType.VISUAL;
            return this;
        }

        public Builder type( FieldSetType type )
        {
            fieldSet.type = type;
            return this;
        }

        public Builder name( String value )
        {
            fieldSet.setName( value );
            return this;
        }

        public Builder label( String value )
        {
            Preconditions.checkNotNull( value, "label cannot be null" );

            fieldSet.label = value;
            return this;
        }

        public Builder required( boolean value )
        {
            fieldSet.required = value;
            return this;
        }

        public Builder immutable( boolean value )
        {
            fieldSet.immutable = value;
            return this;
        }

        public Builder multiple( Multiple multiple )
        {
            fieldSet.multiple = multiple;
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                fieldSet.multiple = new Multiple( 0, 0 );
            }
            else
            {
                fieldSet.multiple = null;
            }
            return this;
        }

        public Builder multiple( int minEntries, int maxEntries )
        {
            Preconditions.checkArgument( minEntries >= 0 );
            Preconditions.checkArgument( maxEntries >= 0 );

            fieldSet.multiple = new Multiple( minEntries, maxEntries );
            return this;
        }

        public Builder customText( String value )
        {
            fieldSet.customText = value;
            return this;
        }

        public Builder helpText( String value )
        {
            fieldSet.helpText = value;
            return this;
        }

        public Builder addConfigItem( ConfigItem value )
        {
            fieldSet.addConfigItem( value );
            return this;
        }

        public FieldSet build()
        {
            Preconditions.checkNotNull( fieldSet.type, "a type for the FieldSet is required" );
            Preconditions.checkNotNull( fieldSet.getName(), "a name for the FieldSet is required" );
            fieldSet.setPath( new ConfigItemPath( fieldSet.getName() ) );
            return fieldSet;
        }
    }
}
