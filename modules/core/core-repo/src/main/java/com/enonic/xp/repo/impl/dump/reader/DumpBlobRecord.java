package com.enonic.xp.repo.impl.dump.reader;

import com.google.common.io.ByteSource;

import com.enonic.xp.repo.impl.dump.blobstore.BlobReference;

public interface DumpBlobRecord
{
    BlobReference getReference();

    ByteSource getBytes();
}
