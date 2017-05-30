package com.enonic.xp.core.impl.dump.model;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.util.BinaryReference;

class Binary
{
    private final BinaryReference reference;

    private final BlobKey blobKey;

    public Binary( final BinaryReference reference, final BlobKey blobKey )
    {
        this.reference = reference;
        this.blobKey = blobKey;
    }
}
