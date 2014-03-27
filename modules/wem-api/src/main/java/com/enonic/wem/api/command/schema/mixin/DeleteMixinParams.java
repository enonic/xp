package com.enonic.wem.api.command.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.mixin.MixinName;

public final class DeleteMixinParams
{
    private MixinName mixinName;

    public MixinName getName()
    {
        return this.mixinName;
    }

    public DeleteMixinParams name( final MixinName mixinName )
    {
        this.mixinName = mixinName;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteMixinParams ) )
        {
            return false;
        }

        final DeleteMixinParams that = (DeleteMixinParams) o;
        return Objects.equal( this.mixinName, that.mixinName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.mixinName );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.mixinName, "mixinName cannot be null" );
    }
}
