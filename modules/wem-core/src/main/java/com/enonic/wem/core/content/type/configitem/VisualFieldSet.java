package com.enonic.wem.core.content.type.configitem;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;


public class VisualFieldSet
    extends ConfigItem
{
    private String label;

    private ConfigItems configItems = new ConfigItems();

    protected VisualFieldSet()
    {
        super( ConfigItemType.VISUAL_FIELD_SET );
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public VisualFieldSet copy()
    {
        final VisualFieldSet copy = (VisualFieldSet) super.copy();
        copy.label = label;
        copy.configItems = configItems.copy();
        return copy;
    }

    public static Builder newVisualFieldSet()
    {
        return new Builder();
    }

    public void addConfigItem( final ConfigItem configItem )
    {
        this.configItems.addConfigItem( configItem );
    }

    ConfigItems getConfigItems()
    {
        return configItems;
    }

    public ConfigItem getConfigItem( final String name )
    {
        return configItems.getConfigItem( name );
    }

    void forwardSetPath( ConfigItemPath path )
    {
        configItems.setPath( path );
    }

    public static class Builder
    {
        private String label;

        private String name;

        private List<ConfigItem> configItems = new ArrayList<ConfigItem>();

        public Builder label( String value )
        {
            this.label = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder add( ConfigItem configItem )
        {
            this.configItems.add( configItem );
            return this;
        }

        public VisualFieldSet build()
        {
            Preconditions.checkNotNull( this.label, "label is required" );
            Preconditions.checkNotNull( this.name, "name is required" );

            VisualFieldSet visualFieldSet = new VisualFieldSet();
            visualFieldSet.label = this.label;
            visualFieldSet.setName( this.name );
            for ( ConfigItem configItem : configItems )
            {
                visualFieldSet.addConfigItem( configItem );
            }
            return visualFieldSet;
        }
    }
}
