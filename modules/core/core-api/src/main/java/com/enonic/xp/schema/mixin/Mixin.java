package com.enonic.xp.schema.mixin;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.BaseSchema;

@Beta
public final class Mixin
    extends BaseSchema<MixinName>
{
    private final Form form;

    private final List<String> allowContentTypes;

    private Mixin( final Builder builder )
    {
        super( builder );
        this.form = builder.formBuilder.build();
        this.allowContentTypes = builder.allowContentTypes;
    }

    public Form getForm()
    {
        return this.form;
    }

    public List<String> getAllowContentTypes()
    {
        return allowContentTypes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Mixin mixin )
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
        return Objects.equals( form, mixin.form ) && Objects.equals( allowContentTypes, mixin.allowContentTypes );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( form, allowContentTypes );
    }

    public static class Builder
        extends BaseSchema.Builder<Builder, MixinName>
    {
        private Form.Builder formBuilder = Form.create();

        private List<String> allowContentTypes = Lists.newArrayList();

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
        public Builder name( final MixinName value )
        {
            super.name( value );
            return this;
        }

        public Builder name( final String value )
        {
            super.name( MixinName.from( value ) );
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

        public Mixin build()
        {
            return new Mixin( this );
        }
    }
}
