package com.enonic.wem.core.content.type.configitem;


import org.elasticsearch.common.base.Preconditions;

public abstract class ConfigItem
{
    private String name;

    private ConfigItemType itemType;

    ConfigItem( final ConfigItemType itemType )
    {
        this.itemType = itemType;
    }

    public ConfigItemType getConfigItemType()
    {
        return itemType;
    }

    void setName( final String name )
    {
        Preconditions.checkArgument( !name.contains( "." ), "name cannot contain punctations: " + name );
        this.name = name;
    }

    public final String getName()
    {
        return name;
    }

    public ConfigItem copy()
    {
        try
        {
            ConfigItem configItem = this.getClass().newInstance();
            configItem.name = name;
            configItem.itemType = itemType;
            return configItem;
        }
        catch ( InstantiationException e )
        {
            throw new RuntimeException( "Failed to copy ConfigItem", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( "Failed to copy ConfigItem", e );
        }
    }

    @Override
    public String toString()
    {
        return name;
    }
}
