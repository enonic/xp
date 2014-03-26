package com.enonic.wem.api.command.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.mixin.MixinNames;

public final class GetMixinsParams
{
    private MixinNames names;

    public MixinNames getNames()
    {
        return this.names;
    }

    public GetMixinsParams names( final MixinNames names )
    {
        this.names = names;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetMixinParams ) )
        {
            return false;
        }

        final GetMixinsParams that = (GetMixinsParams) o;
        return Objects.equal( this.names, that.names );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.names );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.names, "Content type cannot be null" );
    }
}
