package com.enonic.wem.api.blob;

import java.io.InputStream;

public interface Blob
{
    BlobKey getKey();

    long getLength();

    InputStream getStream();
}
