package com.enonic.wem.api.schema.metadata;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class GetMetadataSchemasParams
{
    private MetadataSchemaNames names;

    public MetadataSchemaNames getNames()
    {
        return this.names;
    }

    public GetMetadataSchemasParams names( final MetadataSchemaNames names )
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

        if ( !( o instanceof GetMetadataSchemaParams ) )
        {
            return false;
        }

        final GetMetadataSchemasParams that = (GetMetadataSchemasParams) o;
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
