package com.enonic.wem.core.content.type.configitem;


/**
 *
 */
public abstract class ConfigItem
{
    private String name;

    private ConfigItemPath path;

    private ConfigItemType itemType;

    protected ConfigItem( final ConfigItemType itemType )
    {
        this.itemType = itemType;
    }

    public ConfigItemType getConfigItemType()
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

    void setPath( final ConfigItemPath path )
    {
        this.path = path;
    }

    public ConfigItemPath getPath()
    {
        return path;
    }

    public ConfigItem copy()
    {
        try
        {
            ConfigItem configItem = this.getClass().newInstance();
            configItem.path = path;
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
        ConfigItemPath configItemPath = getPath();
        if ( configItemPath != null )
        {
            return configItemPath.toString();
        }
        else
        {
            return getName() + "?";
        }
    }
}
