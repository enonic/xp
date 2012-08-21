package com.enonic.wem.core.content.type.configitem;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.data.Entries;
import com.enonic.wem.core.content.data.Entry;

public class FieldSet
    extends ConfigItem
{
    private String label;

    private ConfigItems configItems = new ConfigItems();

    private boolean immutable;

    private final Occurrences occurrences = new Occurrences( 0, 1 );

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
    void setParentPath( final ConfigItemPath parentPath )
    {
        super.setParentPath( parentPath );
        for ( ConfigItem configItem : configItems )
        {
            configItem.setParentPath( this.getPath() );
        }
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
        FieldSet copy = (FieldSet) super.copy();
        copy.label = label;
        copy.immutable = immutable;
        copy.occurrences.setMinOccurences( occurrences.getMinimum() );
        copy.occurrences.setMaxOccurences( occurrences.getMaximum() );
        copy.customText = customText;
        copy.helpText = helpText;
        copy.configItems = configItems.copy();
        return copy;
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

    public boolean breaksRequiredContract( Entries entries )
    {
        Preconditions.checkNotNull( entries, "Given entries is null" );
        //Preconditions.checkArgument( entries.getFieldSet() != null, "Given value have no field" );
        //Preconditions.checkArgument( entries.getFieldSet().equals( this ), "Given value's field is not this" );

        if ( !isRequired() )
        {
            return false;
        }

        if ( entries.size() == 0 )
        {
            return true;
        }

        for ( Entry entry : entries )
        {
            if ( entry.breaksRequiredContract() )
            {
                return false;
            }
        }

        return true;
    }

    public static class Builder
    {
        private String name;

        private String label;

        private boolean immutable;

        private Occurrences occurrences = new Occurrences( 0, 1 );

        private String customText;

        private String helpText;

        private List<ConfigItem> configItems = new ArrayList<ConfigItem>();

        private Builder()
        {

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

        public Builder immutable( boolean value )
        {
            immutable = value;
            return this;
        }

        public Builder occurrences( Occurrences value )
        {
            occurrences = value;
            return this;
        }

        public Builder occurrences( int minOccurrences, int maxOccurrences )
        {
            occurrences = new Occurrences( minOccurrences, maxOccurrences );
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

        public Builder customText( String value )
        {
            customText = value;
            return this;
        }

        public Builder helpText( String value )
        {
            helpText = value;
            return this;
        }

        public Builder add( ConfigItem value )
        {
            configItems.add( value );
            return this;
        }

        public FieldSet build()
        {
            FieldSet fieldSet = new FieldSet();
            fieldSet.setName( name );
            fieldSet.label = label;
            fieldSet.immutable = immutable;
            fieldSet.occurrences.setMinOccurences( occurrences.getMinimum() );
            fieldSet.occurrences.setMaxOccurences( occurrences.getMaximum() );
            fieldSet.customText = customText;
            fieldSet.helpText = helpText;
            for ( ConfigItem configItem : configItems )
            {
                fieldSet.addConfigItem( configItem );
            }

            Preconditions.checkNotNull( fieldSet.getName(), "a name for the FieldSet is required" );
            fieldSet.setPath( new ConfigItemPath( fieldSet.getName() ) );
            return fieldSet;
        }
    }
}
