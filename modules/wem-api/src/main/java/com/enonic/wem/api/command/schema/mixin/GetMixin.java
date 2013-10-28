package com.enonic.wem.api.command.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;

public final class GetMixin
    extends Command<Mixin>
{
    private MixinName name;

    public MixinName getName()
    {
        return this.name;
    }

    public GetMixin name( final MixinName value )
    {
        this.name = value;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetMixin ) )
        {
            return false;
        }

        final GetMixin that = (GetMixin) o;
        return Objects.equal( this.name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.name );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
    }

}
