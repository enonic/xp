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

    boolean isMultiple()
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

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public ConfigItem getConfig( final ConfigItemPath configItemPath )
    {
        return configItems.getConfigItem( configItemPath.getLastElement() );
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

        public FieldSet build()
        {
            Preconditions.checkNotNull( fieldSet.type, "type must be specified" );
            Preconditions.checkNotNull( fieldSet.getName(), "name must be specified" );
            fieldSet.setPath( new ConfigItemPath( fieldSet.getName() ) );
            return fieldSet;
        }
    }
}
