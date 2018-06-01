package com.enonic.xp.schema.xdata;

import java.util.Collection;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.BaseSchema;

public final class XData
    extends BaseSchema<XDataName>
{
    private final Form form;

    private final ImmutableList<String> allowContentTypes;

    private XData( final Builder builder )
    {
        super( builder );
        this.form = builder.formBuilder.build();
        this.allowContentTypes = builder.allowContentTypes.build();
    }

    public Form getForm()
    {
        return this.form;
    }

    public ImmutableList<String> getAllowContentTypes()
    {
        return allowContentTypes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final XData xData )
    {
        return new Builder( xData );
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
        if ( !super.equals( o ) )
        {
            return false;
        }
        final XData xData = (XData) o;
        return Objects.equals( allowContentTypes, xData.allowContentTypes ) && Objects.equals( form, xData.form );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), allowContentTypes, form );
    }

    public static class Builder
        extends BaseSchema.Builder<Builder, XDataName>
    {
        private ImmutableList.Builder<String> allowContentTypes = ImmutableList.builder();

        private Form.Builder formBuilder = Form.create();

        public Builder()
        {
            super();
        }

        public Builder( final XData xData )
        {
            super( xData );
            this.allowContentTypes = ImmutableList.<String>builder().addAll( xData.getAllowContentTypes() );
            this.formBuilder = Form.create( xData.getForm() );
        }

        public Builder allowContentType( final String value )
        {
            this.allowContentTypes.add( value );
            return this;
        }

        public Builder allowContentTypes( final Collection<String> value )
        {
            this.allowContentTypes.addAll( value );
            return this;
        }

        public Builder form( final Form value )
        {
            this.formBuilder = Form.create( value );
            return this;
        }

        public Builder addFormItem( final FormItem value )
        {
            this.formBuilder.addFormItem( value );
            return this;
        }

        public XData build()
        {
            return new XData( this );
        }
    }
}
