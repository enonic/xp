package com.enonic.wem.api.command.content.blob;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.Command;

public final class GetBlob
    extends Command<Blob>
{
    private BlobKey blobKey;

    public GetBlob( final BlobKey blobKey )
    {
        this.blobKey = blobKey;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetBlob ) )
        {
            return false;
        }

        final GetBlob that = (GetBlob) o;
        return Objects.equal( this.blobKey, that.blobKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.blobKey );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.blobKey, "blobKey cannot be null" );
    }
}
