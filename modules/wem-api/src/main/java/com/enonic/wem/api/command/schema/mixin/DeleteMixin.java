package com.enonic.wem.api.command.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.mixin.MixinName;

public final class DeleteMixin
    extends Command<DeleteMixinResult>
{
    private MixinName mixinName;

    public MixinName getName()
    {
        return this.mixinName;
    }

    public DeleteMixin name( final MixinName mixinName )
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

        if ( !( o instanceof DeleteMixin ) )
        {
            return false;
        }

        final DeleteMixin that = (DeleteMixin) o;
        return Objects.equal( this.mixinName, that.mixinName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.mixinName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.mixinName, "mixinName cannot be null" );
    }
}
