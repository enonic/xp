package com.enonic.xp.form;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;

@PublicApi
public class InlineMixin
    extends FormItem
{
    private final MixinName mixinName;

    private InlineMixin( Builder builder )
    {
        super(  );

        Preconditions.checkNotNull( builder.mixinName, "mixinName is required" );
        this.mixinName = builder.mixinName;
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.MIXIN_REFERENCE;
    }

    public MixinName getMixinName()
    {
        return mixinName;
    }

    @Override
    public String getName() {
        return mixinName.getLocalName();
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

        final InlineMixin that = (InlineMixin) o;
        return super.equals( o ) && Objects.equals( this.mixinName, that.mixinName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), this.mixinName );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public InlineMixin copy()
    {
        return create( this ).build();
    }

    public static Builder create( final Mixin mixin )
    {
        return new Builder( mixin );
    }

    public static Builder create( final InlineMixin inline )
    {
        return new Builder( inline );
    }

    public static class Builder
    {
        private MixinName mixinName;

        public Builder()
        {
            // default;
        }

        public Builder( InlineMixin source )
        {
            this.mixinName = source.mixinName;
        }

        public Builder( final Mixin mixin )
        {
            this.mixinName = mixin.getName();
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

        public InlineMixin build()
        {
            return new InlineMixin( this );
        }
    }


}
