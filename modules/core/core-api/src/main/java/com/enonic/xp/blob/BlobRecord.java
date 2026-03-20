package com.enonic.xp.blob;

import com.google.common.io.ByteSource;

public interface BlobRecord
{
    BlobKey getKey();

    long getLength();

    ByteSource getBytes();

    long lastModified();
}
