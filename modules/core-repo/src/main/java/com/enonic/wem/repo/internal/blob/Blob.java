package com.enonic.wem.repo.internal.blob;

import java.io.InputStream;

import com.enonic.xp.blob.BlobKey;

public interface Blob
{
    BlobKey getKey();

    long getLength();

    InputStream getStream();
}
