package com.enonic.wem.api.content.schema.content.form;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.MixinFetcher;

/**
 * Mutable.
 */
class FormItems
    implements Iterable<FormItem>
{
    private final FormItem containerFormItem;

    private LinkedHashMap<String, FormItem> formItemByName = new LinkedHashMap<>();

    private LinkedHashMap<String, Layout> layoutByName = new LinkedHashMap<>();

    FormItems( final FormItem containerFormItem )
    {
        this.containerFormItem = containerFormItem;
    }

    FormItemPath getPath()
    {
        if ( containerFormItem == null )
        {
            return FormItemPath.ROOT;
        }
        return containerFormItem.getPath();
    }

    void add( final FormItem formItem )
    {
        if ( formItem.getParent() != null )
        {
            throw new IllegalArgumentException(
                "formItem [" + formItem.getName() + "] already added to: " + formItem.getParent().getPath().toString() );
        }
        Object previous = formItemByName.put( formItem.getName(), formItem );
        Preconditions.checkArgument( previous == null, "FormItem already added: " + formItem );

        formItem.setParent( this );

        if ( formItem instanceof Layout )
        {
            layoutByName.put( formItem.getName(), (Layout) formItem );
        }
    }

    FormItem getFormItem( final FormItemPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            return doForwardGetFormItem( path );
        }
        else
        {
            return doGetFormItem( path.getFirstElement() );
        }
    }

    private FormItem doForwardGetFormItem( final FormItemPath path )
    {
        FormItem foundFormItem = doGetFormItem( path.getFirstElement() );
        if ( foundFormItem == null )
        {
            return null;
        }

        if ( foundFormItem instanceof FormItemSet )
        {
            FormItemSet formItemSet = (FormItemSet) foundFormItem;
            return formItemSet.getFormItem( path.asNewWithoutFirstPathElement() );
        }
        else if ( foundFormItem instanceof MixinReference )
        {
            throw new IllegalArgumentException( "Cannot get formItem [" + path + "] because it's past a MixinReference [" + foundFormItem +
                                                    "], resolve the MixinReference first." );
        }
        else
        {
            return foundFormItem;
        }
    }

    /*private HierarchicalFormItem doGetHierarchicalFormItem( final String name )
    {
        return typeCast( doGetFormItem( name ), HierarchicalFormItem.class );
    }*/

    FormItem doGetFormItem( final String name )
    {
        Preconditions.checkArgument( FormItemPath.hasNotPathElementDivider( name ), "name cannot be a path: %s", name );

        FormItem foundFormItem = formItemByName.get( name );
        if ( foundFormItem == null )
        {
            foundFormItem = searchFormItemInLayouts( name );
        }
        return foundFormItem;
    }

    Input getInput( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), Input.class );
    }

    FormItemSet getFormItemSet( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), FormItemSet.class );
    }

    MixinReference getMixinReference( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), MixinReference.class );
    }

    Layout getLayout( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), Layout.class );
    }

    public Iterator<FormItem> iterator()
    {
        return formItemByName.values().iterator();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        int index = 0;
        final int size = formItemByName.size();
        for ( FormItem entry : formItemByName.values() )
        {
            s.append( entry.getName() );
            if ( index < size - 1 )
            {
                s.append( ", " );
            }
            index++;
        }
        return s.toString();
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
                    Preconditions.checkArgument( mixinReference.getMixinClass() == mixin.getFormItem().getClass(),
                                                 "Mixin expected to be of type %s: " + mixin.getFormItem().getClass().getSimpleName(),
                                                 mixinReference.getMixinClass().getSimpleName() );

                    final FormItem formItemCreatedFromMixin = FormItem.from( mixin, mixinReference );
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
