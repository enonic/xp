package com.enonic.wem.core.version;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;

public class EntityVersionDocument
{
    private final BlobKey blobKey;

    private final EntityId entityId;


    private EntityVersionDocument( final Builder builder )
    {
        this.blobKey = builder.blobKey;
        this.entityId = builder.entityId;
    }

    public BlobKey getId()
    {
        return blobKey;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }


    public static class Builder
    {
        private BlobKey blobKey;

        private EntityId entityId;

        private Builder()
        {
        }

        public Builder blobKey( final BlobKey blobKey )
        {
            this.blobKey = blobKey;
            return this;
        }

        public Builder entityId( final EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public EntityVersionDocument build()
        {
            validate();
            return new EntityVersionDocument( this );
        }

        private void validate()
        {
            Preconditions.checkNotNull( blobKey );
            Preconditions.checkNotNull( entityId );
        }

    }


}
