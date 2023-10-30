package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.io.OutputStream;

public interface BlobHolder
    extends BlobContainer
{
    void copyTo( OutputStream outputStream )
        throws IOException;
}
