package com.enonic.xp.schema.mixin;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.BaseSchema;

@Beta
public class Mixin
    extends BaseSchema<MixinName>
{
    private final Form form;

    protected Mixin( final Builder builder )
    {
        super( builder );
        this.form = builder.formBuilder.build();
    }

    public Form getForm()
    {
        return this.form;
    }

    public static Builder<? extends Builder> create()
    {
        return new Builder();
    }

    public static Builder<? extends Builder> create( final Mixin mixin )
    {
        return new Builder( mixin );
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
        final Mixin mixin = (Mixin) o;
        return Objects.equals( form, mixin.form );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( form );
    }

    public static class Builder<BUILDER extends Builder<BUILDER>>
        extends BaseSchema.Builder<BUILDER, MixinName>
    {
        private Form.Builder formBuilder = Form.create();

        public Builder()
        {
            super();
        }

        public Builder( final Mixin mixin )
        {
            super( mixin );
            this.formBuilder = Form.create( mixin.getForm() );
        }

        @Override
        public BUILDER name( final MixinName value )
        {
            super.name( value );
            return (BUILDER) this;
        }

        public BUILDER name( final String value )
        {
            super.name( MixinName.from( value ) );
            return (BUILDER) this;
        }

        public BUILDER form( final Form value )
        {
            this.formBuilder = Form.create( value );
            return (BUILDER) this;
        }

        public BUILDER addFormItem( final FormItem value )
        {
            this.formBuilder.addFormItem( value );
            return (BUILDER) this;
        }

        public Mixin build()
        {
            return new Mixin( this );
        }
    }
}
