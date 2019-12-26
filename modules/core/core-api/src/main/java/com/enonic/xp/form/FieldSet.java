package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;


@PublicApi
public class FieldSet
    extends Layout
    implements Iterable<FormItem>
{
    private final String label;

    private final String labelI18nKey;

    private final FormItems formItems;

    private FieldSet( final Builder builder )
    {
        super( builder.name );

        Preconditions.checkNotNull( builder.label, "label is required" );

        this.label = builder.label;
        this.labelI18nKey = builder.labelI18nKey;

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

    public String getLabelI18nKey()
    {
        return labelI18nKey;
    }

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItems.iterator();
    }

    @Override
    public FieldSet copy()
    {
        return create( this ).build();
    }

    public void addFormItem( final FormItem formItem )
    {
        this.formItems.add( formItem );
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    @Override
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

        final FieldSet that = (FieldSet) o;
        return super.equals( o ) && Objects.equals( this.label, that.label ) && Objects.equals( this.labelI18nKey, that.labelI18nKey ) &&
            Objects.equals( this.formItems, that.formItems );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), this.label, this.labelI18nKey, this.formItems );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final FieldSet fieldSet )
    {
        return new Builder( fieldSet );
    }

    public static class Builder
    {
        private String label;

        private String labelI18nKey;

        private String name;

        private List<FormItem> formItems = new ArrayList<>();

        private Builder()
        {
            // default
        }

        private Builder( final FieldSet source )
        {
            this.label = source.label;
            this.labelI18nKey = source.labelI18nKey;
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

        public Builder labelI18nKey( String value )
        {
            this.labelI18nKey = value;
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

        public Builder clearFormItems()
        {
            formItems.clear();
            return this;
        }

        public FieldSet build()
        {
            return new FieldSet( this );
        }
    }
}
