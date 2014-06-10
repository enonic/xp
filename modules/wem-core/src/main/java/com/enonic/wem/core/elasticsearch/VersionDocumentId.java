package com.enonic.wem.core.elasticsearch;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;

public class VersionDocumentId
{
    private final String value;

    private final EntityId entityId;

    private final BlobKey blobKey;

    public static final String SEPARATOR = "_";

    public VersionDocumentId( final EntityId entityId, final BlobKey blobKey )
    {
        Preconditions.checkNotNull( entityId );
        Preconditions.checkNotNull( blobKey );

        this.value = entityId + SEPARATOR + blobKey.toString();
        this.entityId = entityId;
        this.blobKey = blobKey;
    }

    @Override
    public String toString()
    {
        return value;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }
}
