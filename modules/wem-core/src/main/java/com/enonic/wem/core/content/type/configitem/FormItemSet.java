package com.enonic.wem.core.content.type.configitem;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class FormItemSet
    extends DirectAccessibleFormItem
{
    private String label;

    private ConfigItems configItems = new ConfigItems();

    private boolean immutable;

    private final Occurrences occurrences = new Occurrences( 0, 1 );

    private String customText;

    private String helpText;

    protected FormItemSet()
    {
        super( ConfigItemType.FIELD_SET );
    }

    @Override
    void setPath( final ConfigItemPath configItemPath )
    {
        super.setPath( configItemPath );
        configItems.setPath( configItemPath );
    }

    public void addConfigItem( final FormItem formItem )
    {
        this.configItems.addConfigItem( formItem );
    }

    public void addField( final Component component )
    {
        Preconditions.checkState( getPath() != null, "Cannot add Field before this FieldSet is added" );

        component.setPath( new ConfigItemPath( getPath(), component.getName() ) );
        this.configItems.addConfigItem( component );
    }

    public void addFieldSet( final FormItemSet formItemSet )
    {
        Preconditions.checkState( getPath() != null, "Cannot add FieldSet before this FieldSet is added" );

        formItemSet.setPath( new ConfigItemPath( getPath(), formItemSet.getName() ) );
        this.configItems.addConfigItem( formItemSet );
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
        for ( DirectAccessibleFormItem configItem : configItems.iterableForDirectAccessConfigItems() )
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
    public FormItemSet copy()
    {
        FormItemSet copy = (FormItemSet) super.copy();
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

    public DirectAccessibleFormItem getConfigItem( final ConfigItemPath configItemPath )
    {
        return configItems.getConfigItem( configItemPath );
    }

    public static class Builder
    {
        private String name;

        private String label;

        private boolean immutable;

        private Occurrences occurrences = new Occurrences( 0, 1 );

        private String customText;

        private String helpText;

        private List<FormItem> formItems = new ArrayList<FormItem>();

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

        public Builder add( FormItem value )
        {
            formItems.add( value );
            return this;
        }

        public FormItemSet build()
        {
            FormItemSet formItemSet = new FormItemSet();
            formItemSet.setName( name );
            formItemSet.label = label;
            formItemSet.immutable = immutable;
            formItemSet.occurrences.setMinOccurences( occurrences.getMinimum() );
            formItemSet.occurrences.setMaxOccurences( occurrences.getMaximum() );
            formItemSet.customText = customText;
            formItemSet.helpText = helpText;
            for ( FormItem formItem : formItems )
            {
                formItemSet.addConfigItem( formItem );
            }

            Preconditions.checkNotNull( formItemSet.getName(), "a name for the FieldSet is required" );
            formItemSet.setPath( new ConfigItemPath( formItemSet.getName() ) );
            return formItemSet;
        }
    }
}
