package com.enonic.wem.core.content.type.formitem;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

public class FormItems
{
    private FormItemPath path;

    private LinkedHashMap<String, FormItem> items = new LinkedHashMap<String, FormItem>();

    private LinkedHashMap<String, HierarchicalFormItem> directAccessibleFormItems = new LinkedHashMap<String, HierarchicalFormItem>();

    private LinkedHashMap<String, VisualFieldSet> visualFieldSets = new LinkedHashMap<String, VisualFieldSet>();

    public FormItems()
    {
        path = new FormItemPath();
    }

    public FormItemPath getPath()
    {
        return path;
    }

    public void addFormItem( final FormItem item )
    {
        if ( item instanceof HierarchicalFormItem )
        {
            ( (HierarchicalFormItem) item ).setPath( new FormItemPath( path, item.getName() ) );
        }

        Object previous = items.put( item.getName(), item );
        Preconditions.checkArgument( previous == null, "FormItem already added: " + item );

        if ( item instanceof VisualFieldSet )
        {
            visualFieldSets.put( item.getName(), (VisualFieldSet) item );
        }
        else if ( item instanceof HierarchicalFormItem )
        {
            directAccessibleFormItems.put( item.getName(), (HierarchicalFormItem) item );
        }
    }

    public void setPath( final FormItemPath path )
    {
        this.path = path;
        for ( final FormItem formItem : items.values() )
        {
            if ( formItem instanceof HierarchicalFormItem )
            {
                ( (HierarchicalFormItem) formItem ).setParentPath( path );
            }
            else if ( formItem instanceof VisualFieldSet )
            {
                ( (VisualFieldSet) formItem ).forwardSetPath( path );
            }
        }
    }

    public HierarchicalFormItem getFormItem( final FormItemPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            HierarchicalFormItem foundConfig = getDirectAccessibleFormItem( path.getFirstElement() );
            Preconditions.checkArgument( foundConfig.getFormItemType() == FormItemType.FORM_ITEM_SET,
                                         "FormItem at path [%s] expected to be of type FormItemSet: " + foundConfig.getFormItemType(),
                                         path );
            //noinspection ConstantConditions
            FormItemSet formItemSet = (FormItemSet) foundConfig;
            return formItemSet.getFormItem( path.asNewWithoutFirstPathElement() );
        }
        else
        {
            return getDirectAccessibleFormItem( path.getFirstElement() );
        }
    }

    public FormItem getFormItem( final String name )
    {
        FormItem foundFormItem = items.get( name );
        if ( foundFormItem == null )
        {
            foundFormItem = searchFormItemInVisualFieldSets( name );
        }
        return foundFormItem;
    }

    public HierarchicalFormItem getDirectAccessibleFormItem( final String name )
    {
        FormItem formItem = getFormItem( name );
        if ( formItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( formItem instanceof HierarchicalFormItem,
                                     "FormItem [%s] in [%s] is not of type DirectAccessibleFormItem: " + formItem.getClass().getName(),
                                     this.getPath(), formItem.getName() );

        //noinspection ConstantConditions
        return (HierarchicalFormItem) formItem;
    }

    public FormItemSet getFormItemSet( final String name )
    {
        final HierarchicalFormItem formItem = getDirectAccessibleFormItem( name );
        if ( formItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( formItem.getFormItemType() == FormItemType.FORM_ITEM_SET ),
                                     "FormItem at path [%s] is not a FormItemSet: " + formItem.getFormItemType(), formItem.getPath() );

        //noinspection ConstantConditions
        return (FormItemSet) formItem;
    }

    public FormItemSet getFormItemSet( final FormItemPath path )
    {
        final HierarchicalFormItem formItem = getFormItem( path );
        if ( formItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( formItem.getFormItemType() == FormItemType.FORM_ITEM_SET ),
                                     "FormItem at path [%s] is not a FormItemSet: " + formItem.getFormItemType(), formItem.getPath() );

        //noinspection ConstantConditions
        return (FormItemSet) formItem;
    }

    public Component getComponent( final String name )
    {
        final HierarchicalFormItem formItem = getDirectAccessibleFormItem( name );
        if ( formItem == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( formItem instanceof Component ),
                                     "FormItem at path [%s] is not a Component: " + formItem.getFormItemType(), formItem.getPath() );

        //noinspection ConstantConditions
        return (Component) formItem;
    }

    public Component getComponent( final FormItemPath path )
    {
        final HierarchicalFormItem formItem = getFormItem( path );
        if ( formItem == null )
        {
            return null;
        }
        Preconditions.checkArgument( ( formItem instanceof Component ),
                                     "FormItem at path [%s] is not a Component: " + formItem.getFormItemType(), formItem.getPath() );

        //noinspection ConstantConditions
        return (Component) formItem;
    }

    public Iterator<FormItem> iterator()
    {
        return items.values().iterator();
    }

    public Iterable<FormItem> iterable()
    {
        return items.values();
    }

    public Iterable<HierarchicalFormItem> iterableForDirectAccessFormItems()
    {
        return directAccessibleFormItems.values();
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
            if ( entry instanceof HierarchicalFormItem )
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
                s.append( visualFieldSet.getFormItems().toString() );
                s.append( "}" );
            }
        }
        return s.toString();
    }

    public FormItems copy()
    {
        FormItems copy = new FormItems();
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

    public void templateReferencesToFormItems( final TemplateFetcher templateFetcher )
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

                    final HierarchicalFormItem formItemCreatedFromTemplate = template.create( templateReference );
                    if ( formItemCreatedFromTemplate instanceof FormItemSet )
                    {
                        FormItemSet formItemSet = (FormItemSet) formItemCreatedFromTemplate;
                        formItemSet.getFormItems().templateReferencesToFormItems( templateFetcher );
                    }

                    items.put( formItem.getName(), formItemCreatedFromTemplate );
                }
            }
        }
    }

    private FormItem searchFormItemInVisualFieldSets( final String name )
    {
        FormItem foundFormItem = null;

        for ( final VisualFieldSet visualFieldSet : visualFieldSets.values() )
        {
            foundFormItem = visualFieldSet.getFormItem( name );
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
