package com.enonic.wem.api.command.content.binary;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.blob.BlobKey;

public final class BlobCommands
{
    public CreateBlob create( final ByteSource byteSource )
    {
        return new CreateBlob( byteSource );
    }

    public GetBlob get( final BlobKey blobKey )
    {
        return new GetBlob( blobKey );
    }
}
