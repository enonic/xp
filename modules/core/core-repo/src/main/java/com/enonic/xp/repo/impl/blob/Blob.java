package com.enonic.xp.repo.impl.blob;

import java.io.InputStream;

public interface Blob
{
    BlobKey getKey();

    long getLength();

    InputStream getStream();
}
