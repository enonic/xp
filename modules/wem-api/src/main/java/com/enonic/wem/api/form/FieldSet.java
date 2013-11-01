package com.enonic.wem.api.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;


public class FieldSet
    extends Layout
    implements Iterable<FormItem>
{
    private final String label;

    private final FormItems formItems;

    private FieldSet( final Builder builder )
    {
        super( builder.name );

        Preconditions.checkNotNull( builder.label, "label is required" );

        this.label = builder.label;
        this.formItems = new FormItems( this );
        for ( final FormItem formItem : builder.formItems )
        {
            this.formItems.add( formItem );
        }
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItems.iterator();
    }

    @Override
    public FieldSet copy()
    {
        return newFieldSet( this ).build();
    }

    public void addFormItem( final FormItem formItem )
    {
        this.formItems.add( formItem );
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    public FormItem getFormItem( final String name )
    {
        return formItems.getFormItem( FormItemPath.from( name ) );
    }

    public Input getInput( final String name )
    {
        return formItems.getInput( FormItemPath.from( name ) );
    }

    public Iterable<FormItem> formItemIterable()
    {
        return formItems;
    }

    public static Builder newFieldSet()
    {
        return new Builder();
    }

    public static Builder newFieldSet( final FieldSet fieldSet )
    {
        return new Builder( fieldSet );
    }

    public static class Builder
    {
        private String label;

        private String name;

        private List<FormItem> formItems = new ArrayList<FormItem>();

        private Builder()
        {
            // default
        }

        private Builder( final FieldSet source )
        {
            this.label = source.label;
            this.name = source.getName();

            for ( final FormItem formItemSource : source.formItems )
            {
                formItems.add( formItemSource.copy() );
            }
        }

        public Builder label( String value )
        {
            this.label = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder addFormItem( FormItem formItem )
        {
            this.formItems.add( formItem );
            return this;
        }

        public Builder addFormItems( Iterable<FormItem> iterable )
        {
            for ( FormItem formItem : iterable )
            {
                formItems.add( formItem );
            }
            return this;
        }


        public FieldSet build()
        {
            return new FieldSet( this );
        }
    }
}
