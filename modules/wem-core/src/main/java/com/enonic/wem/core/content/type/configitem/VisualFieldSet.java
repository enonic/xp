package com.enonic.wem.core.content.type.configitem;

import java.util.ArrayList;
import java.util.List;

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

    public static class Builder
    {
        private String label;

        private List<ConfigItem> configItems = new ArrayList<ConfigItem>();

        public Builder label( String value )
        {
            this.label = value;
            return this;
        }

        public Builder add( ConfigItem configItem )
        {
            this.configItems.add( configItem );
            return this;
        }

        public VisualFieldSet build()
        {
            VisualFieldSet visualFieldSet = new VisualFieldSet();
            visualFieldSet.label = this.label;
            visualFieldSet.setName( this.label );
            for ( ConfigItem configItem : configItems )
            {
                visualFieldSet.addConfigItem( configItem );
            }
            return visualFieldSet;
        }
    }
}
