package com.enonic.wem.api.blob;

import java.io.InputStream;

public interface BlobService
{
    Blob create( InputStream byteSource );

    Blob get( BlobKey blobKey );
}
