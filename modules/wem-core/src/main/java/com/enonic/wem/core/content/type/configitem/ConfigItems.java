package com.enonic.wem.core.content.type.configitem;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

public class ConfigItems
{
    private ConfigItemPath path;

    private LinkedHashMap<String, FormItem> items = new LinkedHashMap<String, FormItem>();

    private LinkedHashMap<String, DirectAccessibleFormItem> directAccessibleConfigItems =
        new LinkedHashMap<String, DirectAccessibleFormItem>();

    private LinkedHashMap<String, VisualFieldSet> visualFieldSets = new LinkedHashMap<String, VisualFieldSet>();

    public ConfigItems()
    {
        path = new ConfigItemPath();
    }

    public ConfigItemPath getPath()
    {
        return path;
    }

    public void addConfigItem( final FormItem item )
    {
        if ( item instanceof DirectAccessibleFormItem )
        {
            ( (DirectAccessibleFormItem) item ).setPath( new ConfigItemPath( path, item.getName() ) );
        }

        Object previous = items.put( item.getName(), item );
        Preconditions.checkArgument( previous == null, "ConfigItem already added: " + item );

        if ( item instanceof VisualFieldSet )
        {
            visualFieldSets.put( item.getName(), (VisualFieldSet) item );
        }
        else if ( item instanceof DirectAccessibleFormItem )
        {
            directAccessibleConfigItems.put( item.getName(), (DirectAccessibleFormItem) item );
        }
    }

    public void setPath( final ConfigItemPath path )
    {
        this.path = path;
        for ( final FormItem formItem : items.values() )
        {
            if ( formItem instanceof DirectAccessibleFormItem )
            {
                ( (DirectAccessibleFormItem) formItem ).setParentPath( path );
            }
            else if ( formItem instanceof VisualFieldSet )
            {
                ( (VisualFieldSet) formItem ).forwardSetPath( path );
            }
        }
    }

    public DirectAccessibleFormItem getConfigItem( final ConfigItemPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            DirectAccessibleFormItem foundConfig = getDirectAccessibleConfigItem( path.getFirstElement() );
            Preconditions.checkArgument( foundConfig.getConfigItemType() == ConfigItemType.FIELD_SET,
                                         "ConfigItem at path [%s] expected to be of type FieldSet: " + foundConfig.getConfigItemType(),
                                         path );
            //noinspection ConstantConditions
            FormItemSet formItemSet = (FormItemSet) foundConfig;
            return formItemSet.getConfigItem( path.asNewWithoutFirstPathElement() );
        }
        else
        {
            return getDirectAccessibleConfigItem( path.getFirstElement() );
        }
    }

    public FormItem getConfigItem( final String name )
    {
        FormItem foundFormItem = items.get( name );
        if ( foundFormItem == null )
        {
            foundFormItem = searchConfigItemInVisualFieldSets( name );
        }
        return foundFormItem;
    }

    public DirectAccessibleFormItem getDirectAccessibleConfigItem( final String name )
    {
        FormItem formItem = getConfigItem( name );
        if ( formItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( formItem instanceof DirectAccessibleFormItem,
                                     "ConfigItem [%s] in [%s] is not of type AccessibleConfigItem: " + formItem.getClass().getName(),
                                     this.getPath(), formItem.getName() );

        //noinspection ConstantConditions
        return (DirectAccessibleFormItem) formItem;
    }

    public FormItemSet getFieldSet( final String name )
    {
        final DirectAccessibleFormItem configItem = getDirectAccessibleConfigItem( name );
        if ( configItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( configItem.getConfigItemType() == ConfigItemType.FIELD_SET ),
                                     "ConfigItem at path [%s] is not a FieldSet: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (FormItemSet) configItem;
    }

    public FormItemSet getFieldSet( final ConfigItemPath path )
    {
        final DirectAccessibleFormItem configItem = getConfigItem( path );
        if ( configItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( configItem.getConfigItemType() == ConfigItemType.FIELD_SET ),
                                     "ConfigItem at path [%s] is not a FieldSet: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (FormItemSet) configItem;
    }

    public Component getField( final String name )
    {
        final DirectAccessibleFormItem configItem = getDirectAccessibleConfigItem( name );
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
        final DirectAccessibleFormItem configItem = getConfigItem( path );
        if ( configItem == null )
        {
            return null;
        }
        Preconditions.checkArgument( ( configItem instanceof Component ),
                                     "ConfigItem at path [%s] is not a Field: " + configItem.getConfigItemType(), configItem.getPath() );

        //noinspection ConstantConditions
        return (Component) configItem;
    }

    public Iterator<FormItem> iterator()
    {
        return items.values().iterator();
    }

    public Iterable<FormItem> iterable()
    {
        return items.values();
    }

    public Iterable<DirectAccessibleFormItem> iterableForDirectAccessConfigItems()
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
        for ( FormItem entry : items.values() )
        {
            if ( entry instanceof DirectAccessibleFormItem )
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
        for ( FormItem ci : this.items.values() )
        {
            FormItem copyOfCi = ci.copy();
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
        for ( final FormItem formItem : items.values() )
        {
            if ( formItem instanceof TemplateReference )
            {
                final TemplateReference templateReference = (TemplateReference) formItem;
                final Template template = templateFetcher.getTemplate( templateReference.getTemplateQualifiedName() );
                if ( template != null )
                {
                    Preconditions.checkArgument( templateReference.getTemplateType() == template.getType(),
                                                 "Template expected to be of type %s: " + template.getType(),
                                                 templateReference.getTemplateType() );

                    final DirectAccessibleFormItem configItemCreatedFromTemplate = template.create( templateReference );
                    if ( configItemCreatedFromTemplate instanceof FormItemSet )
                    {
                        FormItemSet formItemSet = (FormItemSet) configItemCreatedFromTemplate;
                        formItemSet.getConfigItems().templateReferencesToConfigItems( templateFetcher );
                    }

                    items.put( formItem.getName(), configItemCreatedFromTemplate );
                }
            }
        }
    }

    private FormItem searchConfigItemInVisualFieldSets( final String name )
    {
        FormItem foundFormItem = null;

        for ( final VisualFieldSet visualFieldSet : visualFieldSets.values() )
        {
            foundFormItem = visualFieldSet.getConfigItem( name );
            if ( foundFormItem != null )
            {
                break;
            }
        }
        return foundFormItem;
    }

    public Iterable<FormItem> getIterable()
    {
        return items.values();
    }
}
