package com.enonic.xp.form;


import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

final class FormItems
    implements Iterable<FormItem>
{
    private final FormItem containerFormItem;

    private final ImmutableMap<String, FormItem> formItemByName;

    FormItems( final FormItem containerFormItem, final Collection<? extends FormItem> formItems )
    {
        final AtomicInteger unnamedCounter = new AtomicInteger( 1 );

        this.containerFormItem = containerFormItem;
        this.formItemByName = formItems.stream()
            .map( this::setParent )
            .collect( ImmutableMap.toImmutableMap(
                ( fi ) -> fi.getName().isEmpty() ? "__unnamed" + unnamedCounter.getAndIncrement() : fi.getName(), Function.identity() ) );
    }

    FormItemPath getPath()
    {
        return containerFormItem == null ? FormItemPath.ROOT : containerFormItem.getPath();
    }

    FormItem getItemByName( String name )
    {
        return this.formItemByName.get( name );
    }

    private FormItem setParent( final FormItem formItem )
    {
        if ( formItem.getParent() != null )
        {
            throw new IllegalArgumentException(
                "formItem [" + formItem.getName() + "] already added to: " + formItem.getParent().getPath().toString() );
        }
        formItem.setParent( this );
        return formItem;
    }

    FormItem getFormItem( final FormItemPath path )
    {
        Objects.requireNonNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must have at least one element" );

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

        if ( foundFormItem instanceof final FormItemSet formItemSet )
        {
            return formItemSet.getFormItem( path.asNewWithoutFirstPathElement() );
        }
        else if ( foundFormItem instanceof final FormOptionSet formOptionSet )
        {
            return formOptionSet.getFormItem( path.asNewWithoutFirstPathElement() );
        }
        else if ( foundFormItem instanceof final FormOptionSetOption formOptionSetOption )
        {
            return formOptionSetOption.getFormItem( path.asNewWithoutFirstPathElement() );
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

    FormItem doGetFormItem( final String name )
    {
        Preconditions.checkArgument( FormItemPath.hasNotPathElementDivider( name ), "name cannot be a path: %s", name );

        FormItem foundFormItem = formItemByName.get( name );
        if ( foundFormItem == null )
        {
            return searchFormItemInLayouts( name );
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

    InlineMixin getInlineMixin( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), InlineMixin.class );
    }

    FormOptionSet getOptionSet( final FormItemPath path )
    {
        return typeCast( getFormItem( path ), FormOptionSet.class );
    }

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItemByName.values().iterator();
    }

    int size()
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
        return formItemByName.values()
            .stream()
            .filter( formItem -> formItem instanceof FieldSet )
            .map( formItem -> (FieldSet) formItem )
            .map( layout -> layout.getFormItem( name ) )
            .filter( Objects::nonNull )
            .findFirst()
            .orElse( null );
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
                                     "FormItem [%s] in [%s] is not of type %s: %s", this.getPath(),
                                     formItem.getName(), type.getSimpleName(), formItem.getClass().getName() );
    }
}
