package com.enonic.wem.api.schema.metadata;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public final class GetMetadataSchemaParams
{
    private MetadataSchemaName name;

    private boolean notFoundAsException = false;

    public GetMetadataSchemaParams( final MetadataSchemaName name )
    {
        this.name = name;
    }

    public GetMetadataSchemaParams name( final MetadataSchemaName value )
    {
        this.name = value;
        return this;
    }

    public GetMetadataSchemaParams notFoundAsException()
    {
        notFoundAsException = true;
        return this;
    }

    public GetMetadataSchemaParams notFoundAsNull()
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

        if ( !( o instanceof GetMetadataSchemaParams ) )
        {
            return false;
        }

        final GetMetadataSchemaParams that = (GetMetadataSchemaParams) o;
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

    public MetadataSchemaName getName()
    {
        return this.name;
    }

    public boolean isNotFoundAsException()
    {
        return notFoundAsException;
    }
}
