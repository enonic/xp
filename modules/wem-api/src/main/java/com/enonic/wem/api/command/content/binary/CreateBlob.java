package com.enonic.wem.api.command.content.binary;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.Command;

public final class CreateBlob
    extends Command<BlobKey>
{
    private ByteSource byteSource;

    public CreateBlob( final ByteSource byteSource )
    {
        this.byteSource = byteSource;
    }

    public ByteSource getByteSource()
    {
        return byteSource;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateBlob ) )
        {
            return false;
        }

        final CreateBlob that = (CreateBlob) o;
        return Objects.equal( this.byteSource, that.byteSource );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.byteSource );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.byteSource, "byteSource cannot be null" );
    }

}
