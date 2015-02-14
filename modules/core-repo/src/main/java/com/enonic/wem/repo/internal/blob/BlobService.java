package com.enonic.wem.repo.internal.blob;

import java.io.InputStream;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;

public interface BlobService
{
    Blob create( InputStream byteSource );

    Blob get( BlobKey blobKey );

    ByteSource getByteSource( final BlobKey blobKey );
}
