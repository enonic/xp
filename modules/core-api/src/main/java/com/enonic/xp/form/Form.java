package com.enonic.xp.form;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;


@Beta
public final class Form
    implements Iterable<FormItem>
{
    private final FormItems formItems;

    private Form( final Builder builder )
    {
        if ( builder.formItems != null )
        {
            this.formItems = builder.formItems;
        }
        else
        {
            this.formItems = new FormItems( null );
            for ( final FormItem formItem : builder.formItemsList )
            {
                this.formItems.add( formItem );
            }
        }
    }

    public void addFormItem( final FormItem formItem )
    {
        this.formItems.add( formItem );
    }

    public FormItem getFormItem( final String path )
    {
        return formItems.getFormItem( FormItemPath.from( path ) );
    }

    public FormItem getFormItem( final FormItemPath path )
    {
        return formItems.getFormItem( path );
    }

    public FormItemSet getFormItemSet( final String path )
    {
        return formItems.getFormItemSet( FormItemPath.from( path ) );
    }

    public FormItemSet getFormItemSet( final FormItemPath path )
    {
        return formItems.getFormItemSet( path );
    }

    public Input getInput( final String path )
    {
        return formItems.getInput( FormItemPath.from( path ) );
    }

    public Input getInput( final FormItemPath path )
    {
        return formItems.getInput( path );
    }

    public InlineMixin getInlineMixin( final String name )
    {
        return formItems.getInlineMixin( FormItemPath.from( name ) );
    }

    public InlineMixin getInlineMixin( final FormItemPath formItemPath )
    {
        return formItems.getInlineMixin( formItemPath );
    }

    public FormItems getFormItems()
    {
        return formItems;
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
        return Objects.equal( this.formItems, that.formItems );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.formItems );
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper( this );
        s.add( "formItems", formItems );
        return s.toString();
    }

    public Form copy()
    {
        return create( this ).build();
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

    public static class Builder
    {
        private FormItems formItems;

        private List<FormItem> formItemsList;

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

        public Builder addFormItems( final Iterable<FormItem> formItems )
        {
            for ( FormItem formItem : formItems )
            {
                addFormItem( formItem );
            }
            return this;
        }

        public Builder addFormItems( final FormItems formItems )
        {
            this.formItems = formItems;
            return this;
        }

        public Form build()
        {
            return new Form( this );
        }
    }
}
