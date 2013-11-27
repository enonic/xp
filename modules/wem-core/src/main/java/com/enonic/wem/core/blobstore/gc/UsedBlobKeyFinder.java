package com.enonic.wem.core.blobstore.gc;

import java.util.Set;

import com.enonic.wem.api.blob.BlobKey;

public interface UsedBlobKeyFinder
{
    public Set<BlobKey> findKeys()
        throws Exception;
}
