package com.enonic.xp.schema.formfragment;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.BaseSchema;

@PublicApi
public final class FormFragmentDescriptor
    extends BaseSchema<FormFragmentName>
{
    private final Form form;

    private FormFragmentDescriptor( final Builder builder )
    {
        super( builder );
        this.form = builder.formBuilder.build();
    }

    public Form getForm()
    {
        return this.form;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final FormFragmentDescriptor descriptor )
    {
        return new Builder( descriptor );
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
        final FormFragmentDescriptor descriptor = (FormFragmentDescriptor) o;
        return Objects.equals( form, descriptor.form );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( super.hashCode(), form );
    }

    public static final class Builder
        extends BaseSchema.Builder<Builder, FormFragmentName>
    {
        private Form.Builder formBuilder;

        private Builder()
        {
            super();
            this.formBuilder = Form.create();
        }

        private Builder( final FormFragmentDescriptor descriptor )
        {
            super( descriptor );
            this.formBuilder = Form.create( descriptor.getForm() );
        }

        @Override
        public Builder name( final FormFragmentName value )
        {
            super.name( value );
            return this;
        }

        public Builder name( final String value )
        {
            super.name( FormFragmentName.from( value ) );
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

        public FormFragmentDescriptor build()
        {
            return new FormFragmentDescriptor( this );
        }
    }
}
