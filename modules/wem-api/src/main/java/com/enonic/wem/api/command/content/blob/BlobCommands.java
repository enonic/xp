package com.enonic.wem.api.command.content.blob;

import java.io.InputStream;

import com.enonic.wem.api.blob.BlobKey;

public final class BlobCommands
{
    public CreateBlob create( final InputStream byteSource )
    {
        return new CreateBlob( byteSource );
    }

    public GetBlob get( final BlobKey blobKey )
    {
        return new GetBlob( blobKey );
    }
}
