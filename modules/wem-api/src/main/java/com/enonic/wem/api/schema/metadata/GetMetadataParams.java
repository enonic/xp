package com.enonic.wem.api.schema.metadata;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public final class GetMetadataParams
{
    private MetadataName name;

    private boolean notFoundAsException = false;

    public GetMetadataParams( final MetadataName name )
    {
        this.name = name;
    }

    public GetMetadataParams name( final MetadataName value )
    {
        this.name = value;
        return this;
    }

    public GetMetadataParams notFoundAsException()
    {
        notFoundAsException = true;
        return this;
    }

    public GetMetadataParams notFoundAsNull()
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

        if ( !( o instanceof GetMetadataParams ) )
        {
            return false;
        }

        final GetMetadataParams that = (GetMetadataParams) o;
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

    public MetadataName getName()
    {
        return this.name;
    }

    public boolean isNotFoundAsException()
    {
        return notFoundAsException;
    }
}
