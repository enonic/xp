package com.enonic.xp.schema.mixin;

import com.google.common.annotations.Beta;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.schema.BaseSchema;

@Beta
public final class Mixin
    extends BaseSchema<MixinName>
{
    private final FormItems formItems;

    private Mixin( final Builder builder )
    {
        super( builder );
        this.formItems = builder.formItems;
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Mixin mixin )
    {
        return new Builder( mixin );
    }

    public static class Builder
        extends BaseSchema.Builder<Builder, MixinName>
    {
        private FormItems formItems = new FormItems();

        public Builder()
        {
            super();
        }

        public Builder( final Mixin mixin )
        {
            super( mixin );
            this.formItems = mixin.formItems;
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

        public Builder formItems( FormItems value )
        {
            this.formItems = value;
            return this;
        }

        public Builder addFormItem( FormItem value )
        {
            this.formItems.add( value );
            return this;
        }

        public Mixin build()
        {
            return new Mixin( this );
        }
    }
}
