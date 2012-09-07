package com.enonic.wem.core.content.type.configitem;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

public class ConfigItems
{
    private ConfigItemPath path;

    private LinkedHashMap<String, ConfigItem> items = new LinkedHashMap<String, ConfigItem>();

    private LinkedHashMap<String, DirectAccessibleConfigItem> directAccessibleConfigItems =
        new LinkedHashMap<String, DirectAccessibleConfigItem>();

    private LinkedHashMap<String, VisualFieldSet> visualFieldSets = new LinkedHashMap<String, VisualFieldSet>();

    public ConfigItems()
    {
        path = new ConfigItemPath();
    }

    public ConfigItemPath getPath()
    {
        return path;
    }

    public void addConfigItem( final ConfigItem item )
    {
        if ( item instanceof DirectAccessibleConfigItem )
        {
            ( (DirectAccessibleConfigItem) item ).setPath( new ConfigItemPath( path, item.getName() ) );
        }

        Object previous = items.put( item.getName(), item );
        Preconditions.checkArgument( previous == null, "ConfigItem already added: " + item );

        if ( item instanceof VisualFieldSet )
        {
            visualFieldSets.put( item.getName(), (VisualFieldSet) item );
        }
        else if ( item instanceof DirectAccessibleConfigItem )
        {
            directAccessibleConfigItems.put( item.getName(), (DirectAccessibleConfigItem) item );
        }
    }

    public void setPath( final ConfigItemPath path )
    {
        this.path = path;
        for ( final ConfigItem configItem : items.values() )
        {
            if ( configItem instanceof DirectAccessibleConfigItem )
            {
                ( (DirectAccessibleConfigItem) configItem ).setParentPath( path );
            }
            else if ( configItem instanceof VisualFieldSet )
            {
                ( (VisualFieldSet) configItem ).forwardSetPath( path );
            }
        }
    }

    public DirectAccessibleConfigItem getConfigItem( final ConfigItemPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            DirectAccessibleConfigItem foundConfig = getDirectAccessibleConfigItem( path.getFirstElement() );
            Preconditions.checkArgument( foundConfig.getConfigItemType() == ConfigItemType.FIELD_SET,
                                         "ConfigItem at path [%s] expected to be of type FieldSet: " + foundConfig.getConfigItemType(),
                                         path );
            //noinspection ConstantConditions
            FieldSet fieldSet = (FieldSet) foundConfig;
            return fieldSet.getConfigItem( path.asNewWithoutFirstPathElement() );
        }
        else
        {
            return getDirectAccessibleConfigItem( path.getFirstElement() );
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

    public DirectAccessibleConfigItem getDirectAccessibleConfigItem( final String name )
    {
        ConfigItem configItem = getConfigItem( name );
        if ( configItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( configItem instanceof DirectAccessibleConfigItem,
                                     "ConfigItem [%s] in [%s] is not of type AccessibleConfigItem: " + configItem.getClass().getName(),
                                     this.getPath(), configItem.getName() );

        //noinspection ConstantConditions
        return (DirectAccessibleConfigItem) configItem;
    }

    public FieldSet getFieldSet( final String name )
    {
        final DirectAccessibleConfigItem configItem = getDirectAccessibleConfigItem( name );
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
        final DirectAccessibleConfigItem configItem = getConfigItem( path );
        if ( configItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( configItem.getConfigItemType() == ConfigItemType.FIELD_SET ),
                                     "ConfigItem at path [%s] is not a FieldSet: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (FieldSet) configItem;
    }

    public Component getField( final String name )
    {
        final DirectAccessibleConfigItem configItem = getDirectAccessibleConfigItem( name );
        if ( configItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( configItem instanceof Component ),
                                     "ConfigItem at path [%s] is not a Field: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (Component) configItem;
    }

    public Component getField( final ConfigItemPath path )
    {
        final DirectAccessibleConfigItem configItem = getConfigItem( path );
        if ( configItem == null )
        {
            return null;
        }
        Preconditions.checkArgument( ( configItem instanceof Component ),
                                     "ConfigItem at path [%s] is not a Field: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (Component) configItem;
    }

    public Iterator<ConfigItem> iterator()
    {
        return items.values().iterator();
    }

    public Iterable<ConfigItem> iterable()
    {
        return items.values();
    }

    public Iterable<DirectAccessibleConfigItem> iterableForDirectAccessConfigItems()
    {
        return directAccessibleConfigItems.values();
    }

    public int size()
    {
        return items.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        int index = 0;
        final int size = items.size();
        for ( ConfigItem entry : items.values() )
        {
            if ( entry instanceof DirectAccessibleConfigItem )
            {
                s.append( entry.getName() );
                if ( index < size - 1 )
                {
                    s.append( ", " );
                }
                index++;
            }
            else if ( entry instanceof VisualFieldSet )
            {
                VisualFieldSet visualFieldSet = (VisualFieldSet) entry;
                s.append( visualFieldSet.getName() ).append( "{" );
                s.append( visualFieldSet.getConfigItems().toString() );
                s.append( "}" );
            }
        }
        return s.toString();
    }

    public ConfigItems copy()
    {
        ConfigItems copy = new ConfigItems();
        copy.path = path;
        for ( ConfigItem ci : this.items.values() )
        {
            ConfigItem copyOfCi = ci.copy();
            copy.items.put( copyOfCi.getName(), copyOfCi );

            if ( copyOfCi instanceof VisualFieldSet )
            {
                copy.visualFieldSets.put( copyOfCi.getName(), (VisualFieldSet) copyOfCi );
            }
        }
        return copy;
    }

    public void templateReferencesToConfigItems( final TemplateFetcher templateFetcher )
    {
        for ( final ConfigItem configItem : items.values() )
        {
            if ( configItem instanceof TemplateReference )
            {
                final TemplateReference templateReference = (TemplateReference) configItem;
                final Template template = templateFetcher.getTemplate( templateReference.getTemplateQualifiedName() );
                if ( template != null )
                {
                    Preconditions.checkArgument( templateReference.getTemplateType() == template.getType(),
                                                 "Template expected to be of type %s: " + template.getType(),
                                                 templateReference.getTemplateType() );

                    final DirectAccessibleConfigItem configItemCreatedFromTemplate = template.create( templateReference );
                    if ( configItemCreatedFromTemplate instanceof FieldSet )
                    {
                        FieldSet fieldSet = (FieldSet) configItemCreatedFromTemplate;
                        fieldSet.getConfigItems().templateReferencesToConfigItems( templateFetcher );
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

    public Iterable<ConfigItem> getIterable()
    {
        return items.values();
    }
}
