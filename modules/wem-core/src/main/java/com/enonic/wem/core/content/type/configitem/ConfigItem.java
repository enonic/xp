package com.enonic.wem.core.content.type.configitem;

import org.elasticsearch.common.base.Preconditions;

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
        Preconditions.checkArgument( !name.contains( "." ), "name cannot contain punctations: " + name );
        this.name = name;
    }

    public final String getName()
    {
        return name;
    }

    void setPath( final ConfigItemPath path )
    {
        Preconditions.checkArgument( name.equals( path.getLastElement() ),
                                     "Last element of path must be equal to name [%s]: " + path.getLastElement(), name );
        this.path = path;
    }

    void setParentPath( final ConfigItemPath parentPath )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );

        if ( this.path == null || this.path.elementCount() == 0 )
        {
            throw new IllegalStateException( "Cannot set parent path unless there is already an existing path" );
        }

        this.path = new ConfigItemPath( parentPath, this.path.getLastElement() );
    }

    public final ConfigItemPath getPath()
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
