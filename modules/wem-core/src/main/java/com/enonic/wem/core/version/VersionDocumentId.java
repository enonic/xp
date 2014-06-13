package com.enonic.wem.core.version;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;

public class VersionDocumentId
{
    private final String value;

    public static final String SEPARATOR = "_";

    public VersionDocumentId( final EntityId entityId, final BlobKey blobKey )
    {
        Preconditions.checkNotNull( entityId );
        Preconditions.checkNotNull( blobKey );

        this.value = entityId + SEPARATOR + blobKey.toString();

    }

    @Override
    public String toString()
    {
        return value;
    }

}
