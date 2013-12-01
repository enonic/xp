package com.enonic.wem.api.form;


import java.util.Objects;

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

        final MixinReference that = (MixinReference) o;
        return super.equals( o ) && Objects.equals( this.mixinName, that.mixinName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), this.mixinName );
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
            this.name = mixin.getName().toString();
            this.mixinName = mixin.getName();
        }

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder mixin( final Mixin mixin )
        {
            this.mixinName = mixin.getName();
            return this;
        }

        public Builder mixin( String name )
        {
            this.mixinName = MixinName.from( name );
            return this;
        }

        public Builder mixin( MixinName mixinName )
        {
            this.mixinName = mixinName;
            return this;
        }

        public MixinReference build()
        {
            return new MixinReference( this );
        }
    }


}
