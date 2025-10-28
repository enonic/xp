package com.enonic.xp.schema.xdata;

import java.util.Objects;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.BaseSchema;

public final class MixinDescriptor
    extends BaseSchema<MixinName>
{
    private final Form form;

    private MixinDescriptor( final Builder builder )
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

    public static Builder create( final MixinDescriptor descriptor )
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
        final MixinDescriptor descriptor = (MixinDescriptor) o;
        return Objects.equals( form, descriptor.form );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), form );
    }

    public static final class Builder
        extends BaseSchema.Builder<Builder, MixinName>
    {
        private Form.Builder formBuilder = Form.create();

        private Builder()
        {
            super();
        }

        private Builder( final MixinDescriptor mixin )
        {
            super( mixin );
            this.formBuilder = Form.create( mixin.getForm() );
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

        public MixinDescriptor build()
        {
            return new MixinDescriptor( this );
        }
    }
}
