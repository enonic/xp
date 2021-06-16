package com.enonic.xp.form;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

/**
 * Mutable.
 */
@PublicApi
public final class FormItems
    implements Iterable<FormItem>
{
    private final FormItem containerFormItem;

    private final LinkedHashMap<String, FormItem> formItemByName = new LinkedHashMap<>();

    private final LinkedHashMap<String, Layout> layoutByName = new LinkedHashMap<>();

    public FormItems()
    {
        this.containerFormItem = null;
    }

    public FormItems( final FormItem containerFormItem )
    {
        this.containerFormItem = containerFormItem;
    }

    public FormItemPath getPath()
    {
        if ( containerFormItem == null )
        {
            return FormItemPath.ROOT;
        }
        return containerFormItem.getPath();
    }

    public FormItem getItemByName( String name )
    {
        return this.formItemByName.get( name );
    }

    public void add( final FormItem formItem )
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
        return getFormItem( path, false );
    }

    FormItem getFormItem( final FormItemPath path, final boolean skipLayout )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            return doForwardGetFormItem( path, skipLayout );
        }
        else
        {
            return doGetFormItem( path.getFirstElement(), skipLayout );
        }
    }

    private FormItem doForwardGetFormItem( final FormItemPath path, final boolean skipLayout )
    {
        FormItem foundFormItem = doGetFormItem( path.getFirstElement(), skipLayout );
        if ( foundFormItem == null )
        {
            return null;
        }

        if ( foundFormItem instanceof FormItemSet )
        {
            FormItemSet formItemSet = (FormItemSet) foundFormItem;
            return formItemSet.getFormItem( path.asNewWithoutFirstPathElement() );
        }
        else if ( foundFormItem instanceof FormOptionSet )
        {
            FormOptionSet formOptionSet = (FormOptionSet) foundFormItem;
            return formOptionSet.getFormItems().getFormItem( path.asNewWithoutFirstPathElement() );
        }
        else if ( foundFormItem instanceof FormOptionSetOption )
        {
            FormOptionSetOption formOptionSetOption = (FormOptionSetOption) foundFormItem;
            return formOptionSetOption.getFormItems().getFormItem( path.asNewWithoutFirstPathElement() );
        }
        else if ( foundFormItem instanceof InlineMixin )
        {
            throw new IllegalArgumentException( "Cannot get formItem [" + path + "] because it's past a InlineMixin [" + foundFormItem +
                                                    "], resolve the InlineMixin first." );
        }
        else
        {
            return foundFormItem;
        }
    }

    FormItem doGetFormItem( final String name, final boolean skipLayout )
    {
        Preconditions.checkArgument( FormItemPath.hasNotPathElementDivider( name ), "name cannot be a path: %s", name );

        FormItem foundFormItem = formItemByName.get( name );
        if ( foundFormItem == null || ( skipLayout && foundFormItem.getType() == FormItemType.LAYOUT ) )
        {
            foundFormItem = searchFormItemInLayouts( name );
        }
        return foundFormItem;
    }

    public Input getInput( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), Input.class );
    }

    public Input getInput( final FormItemPath path, final boolean skipLayout )
    {
        return typeCast( getFormItem( path, skipLayout ), Input.class );
    }

    FormItemSet getFormItemSet( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), FormItemSet.class );
    }

    InlineMixin getInlineMixin( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), InlineMixin.class );
    }

    public Layout getLayout( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), Layout.class );
    }

    FormOptionSet getOptionSet( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), FormOptionSet.class );
    }

    FormOptionSetOption getOptionSetOption( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), FormOptionSetOption.class );
    }

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItemByName.values().iterator();
    }

    public int size()
    {
        return formItemByName.size();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final FormItems that = (FormItems) o;
        return Objects.equals( this.formItemByName, that.formItemByName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.formItemByName );
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        int index = 0;
        final int size = formItemByName.size();
        for ( FormItem formItem : formItemByName.values() )
        {
            s.append( formItem.getName() );
            if ( index < size - 1 )
            {
                s.append( ", " );
            }
            index++;
        }
        return s.toString();
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
