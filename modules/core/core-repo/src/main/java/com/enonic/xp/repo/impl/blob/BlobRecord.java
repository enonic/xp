package com.enonic.xp.repo.impl.blob;

import com.google.common.io.ByteSource;

public interface BlobRecord
{
    BlobKey getKey();

    long getLength();

    ByteSource getBytes();
}
