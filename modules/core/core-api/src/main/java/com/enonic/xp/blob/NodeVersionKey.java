package com.enonic.xp.blob;

import java.util.Objects;

import com.google.common.base.Preconditions;

public class NodeVersionKey
{
    private final BlobKey nodeBlobKey;

    private final BlobKey indexConfigBlobKey;

    private NodeVersionKey( final Builder builder )
    {
        nodeBlobKey = builder.nodeBlobKey;
        indexConfigBlobKey = builder.indexConfigBlobKey;
    }

    public BlobKey getNodeBlobKey()
    {
        return nodeBlobKey;
    }

    public BlobKey getIndexConfigBlobKey()
    {
        return indexConfigBlobKey;
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
        return Objects.equals( nodeBlobKey, that.nodeBlobKey ) && Objects.equals( indexConfigBlobKey, that.indexConfigBlobKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( nodeBlobKey, indexConfigBlobKey );
    }

    public static NodeVersionKey from( final BlobKey nodeBlobKey, final BlobKey indexConfigBlobKey )
    {
        return create().
            nodeBlobKey( nodeBlobKey ).
            indexConfigBlobKey( indexConfigBlobKey ).
            build();
    }

    public static NodeVersionKey from( final String nodeBlobKey, final String indexConfigBlobKey )
    {
        return from( BlobKey.from( nodeBlobKey ), BlobKey.from( indexConfigBlobKey ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private BlobKey nodeBlobKey;

        private BlobKey indexConfigBlobKey;

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

        private void validate()
        {
            Preconditions.checkNotNull( nodeBlobKey, "nodeBlobKey cannot be null" );
            Preconditions.checkNotNull( indexConfigBlobKey, "indexConfigBlobKey cannot be null" );
        }

        public NodeVersionKey build()
        {
            validate();
            return new NodeVersionKey( this );
        }
    }
}
