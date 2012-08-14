package com.enonic.wem.core.content.type.configitem;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.Template;

public class ConfigItems
    implements Iterable<ConfigItem>
{
    private ConfigItemPath path;

    private LinkedHashMap<String, ConfigItem> items = new LinkedHashMap<String, ConfigItem>();

    public ConfigItems()
    {
        path = new ConfigItemPath();
    }

    public ConfigItemPath getPath()
    {
        return path;
    }

    public void setPath( final ConfigItemPath path )
    {
        this.path = path;
    }

    public void addConfigItem( final ConfigItem item )
    {
        item.setPath( new ConfigItemPath( path, item.getName() ) );
        Object previous = items.put( item.getName(), item );
        Preconditions.checkArgument( previous == null, "ConfigItem already added: " + item );
    }

    public ConfigItem getConfigItem( final ConfigItemPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            ConfigItem foundConfig = items.get( path.getFirstElement() );
            Preconditions.checkArgument( foundConfig instanceof FieldSet,
                                         "ConfigItem at path [%s] expected to be of type FieldSet: " + foundConfig.getConfigItemType(),
                                         path );
            //noinspection ConstantConditions
            FieldSet fieldSet = (FieldSet) foundConfig;
            return fieldSet.getConfig( path.asNewWithoutFirstPathElement() );
        }
        else
        {
            return items.get( path.getFirstElement() );
        }
    }

    public ConfigItem getConfigItem( final String name )
    {
        return items.get( name );
    }

    public FieldSet getFieldSet( final String name )
    {
        final ConfigItem configItem = items.get( name );
        if ( configItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( configItem instanceof FieldSet ),
                                     "ConfigItem at path [%s] is not a FieldSet: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (FieldSet) configItem;
    }

    public FieldSet getFieldSet( final ConfigItemPath path )
    {
        final ConfigItem configItem = getConfigItem( path );
        if ( configItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( configItem instanceof FieldSet ),
                                     "ConfigItem at path [%s] is not a FieldSet: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (FieldSet) configItem;
    }

    public Field getField( final String name )
    {
        final ConfigItem configItem = items.get( name );
        if ( configItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( configItem instanceof Field ),
                                     "ConfigItem at path [%s] is not a Field: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (Field) configItem;
    }

    public Field getField( final ConfigItemPath path )
    {
        final ConfigItem configItem = getConfigItem( path );
        if ( configItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( configItem instanceof Field ),
                                     "ConfigItem at path [%s] is not a Field: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (Field) configItem;
    }

    public Iterator<ConfigItem> iterator()
    {
        return items.values().iterator();
    }

    public int size()
    {
        return items.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( path.toString() );
        s.append( ": " );
        int index = 0;
        final int size = items.size();
        for ( ConfigItem entry : items.values() )
        {
            s.append( entry.getName() );
            if ( index < size - 1 )
            {
                s.append( "," );
            }
            index++;
        }
        return s.toString();
    }

    public ConfigItems copy()
    {
        ConfigItems copy = new ConfigItems();
        copy.path = path;
        for ( ConfigItem ci : this )
        {
            ConfigItem copyOfCi = ci.copy();
            copy.items.put( copyOfCi.getName(), copyOfCi );
        }
        return copy;
    }

    public void templateReferencesToConfigItems( final TemplateReferenceFetcher templateReferenceFetcher )
    {
        for ( ConfigItem configItem : this )
        {
            if ( configItem.getConfigItemType() == ConfigItemType.REFERENCE )
            {
                TemplateReference templateReference = (TemplateReference) configItem;
                Template template = templateReferenceFetcher.getTemplate( templateReference.getTemplateQualifiedName() );
                items.put( templateReference.getName(), template.create( templateReference ) );
            }
        }
    }
}
