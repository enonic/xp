package com.enonic.wem.repo.internal.blob;

import java.io.InputStream;

public interface Blob
{
    BlobKey getKey();

    long getLength();

    InputStream getStream();
}
