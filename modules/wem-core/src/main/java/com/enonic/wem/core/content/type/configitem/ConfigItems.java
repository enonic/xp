package com.enonic.wem.core.content.type.configitem;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

public class ConfigItems
    implements Iterable<ConfigItem>
{
    private ConfigItemPath path;

    private LinkedHashMap<String, ConfigItem> items = new LinkedHashMap<String, ConfigItem>();

    private LinkedHashMap<String, VisualFieldSet> visualFieldSets = new LinkedHashMap<String, VisualFieldSet>();

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
        for ( ConfigItem configItem : items.values() )
        {
            configItem.setParentPath( path );
        }
    }

    public void addConfigItem( final ConfigItem item )
    {
        if ( item.getConfigItemType() != ConfigItemType.VISUAL_FIELD_SET )
        {
            item.setPath( new ConfigItemPath( path, item.getName() ) );
        }

        Object previous = items.put( item.getName(), item );
        Preconditions.checkArgument( previous == null, "ConfigItem already added: " + item );

        if ( item.getConfigItemType() == ConfigItemType.VISUAL_FIELD_SET )
        {
            visualFieldSets.put( item.getName(), (VisualFieldSet) item );
        }
    }

    public ConfigItem getConfigItem( final ConfigItemPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            ConfigItem foundConfig = getConfigItem( path.getFirstElement() );
            Preconditions.checkArgument( foundConfig.getConfigItemType() == ConfigItemType.FIELD_SET,
                                         "ConfigItem at path [%s] expected to be of type FieldSet: " + foundConfig.getConfigItemType(),
                                         path );
            //noinspection ConstantConditions
            FieldSet fieldSet = (FieldSet) foundConfig;
            return fieldSet.getConfig( path.asNewWithoutFirstPathElement() );
        }
        else
        {
            return getConfigItem( path.getFirstElement() );
        }
    }

    public ConfigItem getConfigItem( final String name )
    {
        ConfigItem foundConfig = items.get( name );
        if ( foundConfig == null )
        {
            foundConfig = searchConfigItemInVisualFieldSets( name );
        }
        return foundConfig;
    }

    public FieldSet getFieldSet( final String name )
    {
        final ConfigItem configItem = getConfigItem( name );
        if ( configItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( configItem.getConfigItemType() == ConfigItemType.FIELD_SET ),
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

        Preconditions.checkArgument( ( configItem.getConfigItemType() == ConfigItemType.FIELD_SET ),
                                     "ConfigItem at path [%s] is not a FieldSet: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (FieldSet) configItem;
    }

    public Field getField( final String name )
    {
        final ConfigItem configItem = getConfigItem( name );
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
        if ( path != null )
        {
            s.append( path.toString() );
        }
        else
        {
            s.append( "?" );
        }
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
        for ( final ConfigItem configItem : this )
        {
            if ( configItem.getConfigItemType() == ConfigItemType.REFERENCE )
            {
                final TemplateReference templateReference = (TemplateReference) configItem;
                final Template template = templateReferenceFetcher.getTemplate( templateReference.getTemplateQualifiedName() );
                if ( template != null )
                {
                    Preconditions.checkArgument( templateReference.getTemplateType() == template.getType(),
                                                 "Template expected to be of type %s: " + template.getType(),
                                                 templateReference.getTemplateType() );

                    final ConfigItem configItemCreatedFromTemplate = template.create( templateReference );
                    if ( configItemCreatedFromTemplate instanceof FieldSet )
                    {
                        FieldSet fieldSet = (FieldSet) configItemCreatedFromTemplate;
                        fieldSet.getConfigItems().templateReferencesToConfigItems( templateReferenceFetcher );
                    }

                    items.put( configItem.getName(), configItemCreatedFromTemplate );
                }
            }
        }
    }

    private ConfigItem searchConfigItemInVisualFieldSets( final String name )
    {
        ConfigItem foundConfig = null;

        for ( final VisualFieldSet visualFieldSet : visualFieldSets.values() )
        {
            foundConfig = visualFieldSet.getConfigItem( name );
            if ( foundConfig != null )
            {
                break;
            }
        }
        return foundConfig;
    }
}
