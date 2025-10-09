package com.enonic.xp.node;

import java.util.Objects;

import com.enonic.xp.blob.BlobKey;

public final class NodeVersionKey
{
    private final BlobKey nodeBlobKey;

    private final BlobKey indexConfigBlobKey;

    private final BlobKey accessControlBlobKey;

    private NodeVersionKey( final Builder builder )
    {
        nodeBlobKey = Objects.requireNonNull( builder.nodeBlobKey );
        indexConfigBlobKey = Objects.requireNonNull( builder.indexConfigBlobKey );
        accessControlBlobKey = Objects.requireNonNull( builder.accessControlBlobKey );
    }

    public BlobKey getNodeBlobKey()
    {
        return nodeBlobKey;
    }

    public BlobKey getIndexConfigBlobKey()
    {
        return indexConfigBlobKey;
    }

    public BlobKey getAccessControlBlobKey()
    {
        return accessControlBlobKey;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof NodeVersionKey ) )
        {
            return false;
        }
        final NodeVersionKey that = (NodeVersionKey) o;
        return Objects.equals( nodeBlobKey, that.nodeBlobKey ) && Objects.equals( indexConfigBlobKey, that.indexConfigBlobKey ) &&
            Objects.equals( accessControlBlobKey, that.accessControlBlobKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( nodeBlobKey, indexConfigBlobKey, accessControlBlobKey );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private BlobKey nodeBlobKey;

        private BlobKey indexConfigBlobKey;

        private BlobKey accessControlBlobKey;

        private Builder()
        {
        }

        public Builder nodeBlobKey( final BlobKey nodeBlobKey )
        {
            this.nodeBlobKey = nodeBlobKey;
            return this;
        }

        public Builder indexConfigBlobKey( final BlobKey indexConfigBlobKey )
        {
            this.indexConfigBlobKey = indexConfigBlobKey;
            return this;
        }

        public Builder accessControlBlobKey( final BlobKey accessControlBlobKey )
        {
            this.accessControlBlobKey = accessControlBlobKey;
            return this;
        }

        public NodeVersionKey build()
        {
            return new NodeVersionKey( this );
        }
    }
}
