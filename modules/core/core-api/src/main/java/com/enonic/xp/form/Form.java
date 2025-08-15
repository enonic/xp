package com.enonic.xp.form;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;


@PublicApi
public final class Form
    implements Iterable<FormItem>
{
    private final FormItems formItems;

    private static final Form EMPTY = new Form( List.of() );

    private Form( final List<FormItem> formItemsList )
    {
        this.formItems = new FormItems( null, formItemsList );
        FormValidator.validate( this );
    }

    public FormItem getFormItem( final FormItemPath path )
    {
        return formItems.getFormItem( path );
    }

    public FormItemSet getFormItemSet( final String path )
    {
        return formItems.getFormItemSet( FormItemPath.from( path ) );
    }

    public Input getInput( final String path )
    {
        return formItems.getInput( FormItemPath.from( path ) );
    }

    public InlineMixin getInlineMixin( final String path )
    {
        return formItems.getInlineMixin( FormItemPath.from( path ) );
    }

    public FormOptionSet getOptionSet( final String path )
    {
        return formItems.getOptionSet( FormItemPath.from( path ) );
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

        final Form that = (Form) o;
        return Objects.equals( this.formItems, that.formItems );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.formItems );
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper( this );
        s.add( "formItems", formItems );
        return s.toString();
    }

    public static Form empty()
    {
        return EMPTY;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Form form )
    {
        return new Builder( form );
    }

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItems.iterator();
    }

    public int size()
    {
        return formItems.size();
    }

    public static final class Builder
    {
        private final List<FormItem> formItemsList;

        private Builder()
        {
            this.formItemsList = new ArrayList<>();
        }

        private Builder( final Form source )
        {
            Preconditions.checkNotNull( source, "Given form cannot be null" );

            this.formItemsList = new ArrayList<>();
            for ( FormItem formItem : source.formItems )
            {
                formItemsList.add( formItem.copy() );
            }
        }

        public Builder addFormItem( final FormItem formItem )
        {
            this.formItemsList.add( formItem );
            return this;
        }

        public Builder addFormItems( final Iterable<? extends FormItem> formItems )
        {
            for ( FormItem formItem : formItems )
            {
                addFormItem( formItem );
            }
            return this;
        }

        public Form build()
        {
            return formItemsList.isEmpty() ? EMPTY : new Form( formItemsList );
        }
    }
}
