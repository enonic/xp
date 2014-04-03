package com.enonic.wem.api.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public final class GetMixinParams
{
    private MixinName name;

    private boolean notFoundAsException = false;

    public GetMixinParams( final MixinName name )
    {
        this.name = name;
    }

    public GetMixinParams name( final MixinName value )
    {
        this.name = value;
        return this;
    }

    public GetMixinParams notFoundAsException()
    {
        notFoundAsException = true;
        return this;
    }

    public GetMixinParams notFoundAsNull()
    {
        notFoundAsException = false;
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

        final GetMixinParams that = (GetMixinParams) o;
        return Objects.equal( this.name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.name );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
    }

    public MixinName getName()
    {
        return this.name;
    }

    public boolean isNotFoundAsException()
    {
        return notFoundAsException;
    }
}
