package com.enonic.wem.core.content.type.configitem;

import org.elasticsearch.common.base.Preconditions;

public abstract class DirectAccessibleConfigItem
    extends ConfigItem
{
    private ConfigItemPath path;

    protected DirectAccessibleConfigItem( final ConfigItemType type )
    {
        super( type );
    }

    void setPath( final ConfigItemPath path )
    {
        Preconditions.checkNotNull( path, "Given path is null" );
        Preconditions.checkArgument( getName().equals( path.getLastElement() ),
                                     "Last element of path must be equal to name [%s]: " + path.getLastElement(), getName() );
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

    public DirectAccessibleConfigItem copy()
    {
        final DirectAccessibleConfigItem configItem = (DirectAccessibleConfigItem) super.copy();
        configItem.path = path;
        return configItem;
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
