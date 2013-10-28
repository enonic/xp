package com.enonic.wem.api.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;

public class MixinReference
    extends FormItem
{
    private final MixinName mixinName;

    private MixinReference( Builder builder )
    {
        super( builder.name );

        Preconditions.checkNotNull( builder.mixinName, "mixinName is required" );
        this.mixinName = builder.mixinName;
    }

    public MixinName getMixinName()
    {
        return mixinName;
    }

    public static Builder newMixinReference()
    {
        return new Builder();
    }

    @Override
    public MixinReference copy()
    {
        return newMixinReference( this ).build();
    }

    public static Builder newMixinReference( final Mixin mixin )
    {
        return new Builder( mixin );
    }

    public static Builder newMixinReference( final MixinReference mixinReference )
    {
        return new Builder( mixinReference );
    }

    public static class Builder
    {
        private String name;

        private MixinName mixinName;

        public Builder()
        {
            // default;
        }

        public Builder( MixinReference source )
        {
            this.name = source.getName();
            this.mixinName = source.mixinName;
        }

        public Builder( final Mixin mixin )
        {
            this.mixinName = mixin.getQualifiedName();
        }

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder mixin( final Mixin mixin )
        {
            this.mixinName = mixin.getQualifiedName();
            return this;
        }

        public Builder mixin( String qualifiedName )
        {
            this.mixinName = MixinName.from( qualifiedName );
            return this;
        }

        public Builder mixin( MixinName qualifiedName )
        {
            this.mixinName = qualifiedName;
            return this;
        }

        public MixinReference build()
        {
            return new MixinReference( this );
        }
    }
}
