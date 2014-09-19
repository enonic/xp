package com.enonic.wem.api.schema.metadata;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class GetMetadatasParams
{
    private MetadataNames names;

    public MetadataNames getNames()
    {
        return this.names;
    }

    public GetMetadatasParams names( final MetadataNames names )
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

        if ( !( o instanceof GetMetadataParams ) )
        {
            return false;
        }

        final GetMetadatasParams that = (GetMetadatasParams) o;
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
