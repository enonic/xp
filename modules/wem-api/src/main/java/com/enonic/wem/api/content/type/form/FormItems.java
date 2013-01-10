package com.enonic.wem.api.content.type.form;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

public class FormItems
    implements Iterable<FormItem>
{
    private FormItemPath path;

    private LinkedHashMap<String, FormItem> formItemByName = new LinkedHashMap<String, FormItem>();

    private LinkedHashMap<String, HierarchicalFormItem> hierarchicalFormItemByName = new LinkedHashMap<String, HierarchicalFormItem>();

    private LinkedHashMap<String, Layout> layoutByName = new LinkedHashMap<String, Layout>();

    public FormItems()
    {
        path = new FormItemPath();
    }

    public FormItemPath getPath()
    {
        return path;
    }

    public void add( final FormItem formItem )
    {
        if ( formItem instanceof HierarchicalFormItem )
        {
            ( (HierarchicalFormItem) formItem ).setPath( new FormItemPath( path, formItem.getName() ) );
        }

        Object previous = formItemByName.put( formItem.getName(), formItem );
        Preconditions.checkArgument( previous == null, "FormItem already added: " + formItem );

        if ( formItem instanceof Layout )
        {
            layoutByName.put( formItem.getName(), (Layout) formItem );
        }
        else if ( formItem instanceof HierarchicalFormItem )
        {
            hierarchicalFormItemByName.put( formItem.getName(), (HierarchicalFormItem) formItem );
        }
    }

    public void setPath( final FormItemPath path )
    {
        this.path = path;
        for ( final FormItem formItem : formItemByName.values() )
        {
            if ( formItem instanceof HierarchicalFormItem )
            {
                ( (HierarchicalFormItem) formItem ).setParentPath( path );
            }
            else if ( formItem instanceof FieldSet )
            {
                ( (FieldSet) formItem ).forwardSetPath( path );
            }
        }
    }

    public HierarchicalFormItem getHierarchicalFormItem( final FormItemPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        final String firstPathElement = path.getFirstElement();
        if ( path.elementCount() > 1 )
        {
            HierarchicalFormItem foundConfig = doGetHierarchicalFormItem( firstPathElement );
            if ( foundConfig == null )
            {
                return null;
            }

            if ( foundConfig instanceof FormItemSet )
            {
                FormItemSet formItemSet = (FormItemSet) foundConfig;
                return formItemSet.getHierarchicalFormItem( path.asNewWithoutFirstPathElement() );
            }
            else if ( foundConfig instanceof MixinReference )
            {
                throw new IllegalArgumentException(
                    "Cannot get formItem [" + path + "] because it's past a MixinReference [" + foundConfig +
                        "], resolve the MixinReference first." );
            }
            else
            {
                return foundConfig;
            }
        }
        else
        {
            return doGetHierarchicalFormItem( firstPathElement );
        }
    }

    private HierarchicalFormItem doGetHierarchicalFormItem( final String name )
    {
        return typeCast( doGetFormItem( name ), HierarchicalFormItem.class );
    }

    public FormItem doGetFormItem( final String name )
    {
        Preconditions.checkArgument( FormItemPath.hasNotPathElementDivider( name ), "name cannot be a path: %s", name );

        FormItem foundFormItem = formItemByName.get( name );
        if ( foundFormItem == null )
        {
            foundFormItem = searchFormItemInLayouts( name );
        }
        return foundFormItem;
    }

    public FormItem getFormItem( final String name )
    {
        return doGetFormItem( name );
    }

    public HierarchicalFormItem getFormItem( final FormItemPath path )
    {
        return getHierarchicalFormItem( path );
    }

    public Input getInput( final String name )
    {
        return typeCast( doGetFormItem( name ), Input.class );
    }

    public Input getInput( final FormItemPath path )
    {
        return typeCast( getHierarchicalFormItem( path ), Input.class );
    }

    public FormItemSet getFormItemSet( final String name )
    {
        return typeCast( doGetFormItem( name ), FormItemSet.class );
    }

    public FormItemSet getFormItemSet( final FormItemPath path )
    {
        return typeCast( getHierarchicalFormItem( path ), FormItemSet.class );
    }

    public MixinReference getMixinReference( final String name )
    {
        return typeCast( doGetFormItem( name ), MixinReference.class );
    }

    public MixinReference getMixinReference( final FormItemPath path )
    {
        return typeCast( getHierarchicalFormItem( path ), MixinReference.class );
    }

    public Layout getLayout( final String name )
    {
        return typeCast( doGetFormItem( name ), Layout.class );
    }

    public Iterator<FormItem> iterator()
    {
        return formItemByName.values().iterator();
    }

    public Iterable<FormItem> iterable()
    {
        return formItemByName.values();
    }

    public Iterable<HierarchicalFormItem> iterableForHierarchicalFormItems()
    {
        return hierarchicalFormItemByName.values();
    }

    public int size()
    {
        return formItemByName.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        int index = 0;
        final int size = formItemByName.size();
        for ( FormItem entry : formItemByName.values() )
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
            else if ( entry instanceof FieldSet )
            {
                FieldSet fieldSet = (FieldSet) entry;
                s.append( fieldSet.getName() ).append( "{" );
                s.append( fieldSet.getFormItems().toString() );
                s.append( "}" );
            }
        }
        return s.toString();
    }

    public FormItems copy()
    {
        FormItems copy = new FormItems();
        copy.path = path;
        for ( FormItem ci : this.formItemByName.values() )
        {
            FormItem copyOfCi = ci.copy();
            copy.formItemByName.put( copyOfCi.getName(), copyOfCi );

            if ( copyOfCi instanceof FieldSet )
            {
                copy.layoutByName.put( copyOfCi.getName(), (FieldSet) copyOfCi );
            }
        }
        return copy;
    }

    // TODO: Move method out of here and into it's own class MixinResolver?
    public void mixinReferencesToFormItems( final MixinFetcher mixinFetcher )
    {
        for ( final FormItem formItem : formItemByName.values() )
        {
            if ( formItem instanceof MixinReference )
            {
                final MixinReference mixinReference = (MixinReference) formItem;
                final Mixin mixin = mixinFetcher.getMixin( mixinReference.getQualifiedMixinName() );
                if ( mixin != null )
                {
                    Preconditions.checkArgument( mixinReference.getMixinClass() == mixin.getType(),
                                                 "Mixin expected to be of type %s: " + mixin.getType().getSimpleName(),
                                                 mixinReference.getMixinClass().getSimpleName() );

                    final FormItem formItemCreatedFromMixin = mixin.toFormItem( mixinReference );
                    if ( formItemCreatedFromMixin instanceof FormItemSet )
                    {
                        final FormItemSet set = (FormItemSet) formItemCreatedFromMixin;
                        set.getFormItems().mixinReferencesToFormItems( mixinFetcher );
                    }

                    formItemByName.put( formItem.getName(), formItemCreatedFromMixin );
                }
            }
        }
    }

    private FormItem searchFormItemInLayouts( final String name )
    {
        FormItem foundFormItem = null;

        for ( final Layout layout : layoutByName.values() )
        {
            foundFormItem = layout.getFormItem( name );
            if ( foundFormItem != null )
            {
                break;
            }
        }
        return foundFormItem;
    }

    private <T extends FormItem> T typeCast( final FormItem formItem, final Class<T> type )
    {
        if ( formItem == null )
        {
            return null;
        }
        checkFormItemType( type, formItem );
        //noinspection unchecked
        return (T) formItem;
    }

    private <T extends FormItem> void checkFormItemType( final Class<T> type, final FormItem formItem )
    {
        Preconditions.checkArgument( type.isInstance( formItem ),
                                     "FormItem [%s] in [%s] is not of type %s: " + formItem.getClass().getName(), this.getPath(),
                                     formItem.getName(), type.getSimpleName() );
    }
}
