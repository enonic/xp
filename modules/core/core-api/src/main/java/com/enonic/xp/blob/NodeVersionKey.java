package com.enonic.xp.blob;

import java.util.Objects;

import com.google.common.base.Preconditions;

public class NodeVersionKey
{
    private final BlobKey nodeBlobKey;

    private final BlobKey indexConfigBlobKey;

    private final BlobKey accessControlBlobKey;

    private NodeVersionKey( final Builder builder )
    {
        nodeBlobKey = builder.nodeBlobKey;
        indexConfigBlobKey = builder.indexConfigBlobKey;
        accessControlBlobKey = builder.accessControlBlobKey;
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
        if ( o == null || getClass() != o.getClass() )
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

    public static NodeVersionKey from( final BlobKey nodeBlobKey, final BlobKey indexConfigBlobKey, final BlobKey accessControlBlobKey )
    {
        return create().
            nodeBlobKey( nodeBlobKey ).
            indexConfigBlobKey( indexConfigBlobKey ).
            accessControlBlobKey( accessControlBlobKey ).
            build();
    }

    public static NodeVersionKey from( final String nodeBlobKey, final String indexConfigBlobKey, final String accessControlBlobKey )
    {
        return from( BlobKey.from( nodeBlobKey ), BlobKey.from( indexConfigBlobKey ), BlobKey.from( accessControlBlobKey ) );
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

        private void validate()
        {
            Preconditions.checkNotNull( nodeBlobKey, "nodeBlobKey cannot be null" );
            Preconditions.checkNotNull( indexConfigBlobKey, "indexConfigBlobKey cannot be null" );
            Preconditions.checkNotNull( accessControlBlobKey, "accessControlBlobKey cannot be null" );
        }

        public NodeVersionKey build()
        {
            validate();
            return new NodeVersionKey( this );
        }
    }
}
