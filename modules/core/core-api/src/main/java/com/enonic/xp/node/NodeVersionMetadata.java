package com.enonic.xp.node;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.blob.BlobKeys;

@PublicApi
public final class NodeVersionMetadata
{
    private final NodeVersionId nodeVersionId;

    private final NodeVersionKey nodeVersionKey;

    private final BlobKeys binaryBlobKeys;

    private final NodeId nodeId;

    private final NodePath nodePath;

    private final NodeCommitId nodeCommitId;

    private final Instant timestamp;

    private NodeVersionMetadata( Builder builder )
    {
        nodeVersionId = Objects.requireNonNull( builder.nodeVersionId );
        nodeVersionKey = Objects.requireNonNull( builder.nodeVersionKey );
        binaryBlobKeys = Objects.requireNonNull( builder.binaryBlobKeys );
        nodeId = Objects.requireNonNull( builder.nodeId );
        nodePath = Objects.requireNonNull( builder.nodePath );
        nodeCommitId = builder.nodeCommitId;
        timestamp = Objects.requireNonNull( builder.timestamp );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( NodeVersionMetadata nodeVersionMetadata )
    {
        return new Builder( nodeVersionMetadata );
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public NodeVersionKey getNodeVersionKey()
    {
        return nodeVersionKey;
    }

    public BlobKeys getBinaryBlobKeys()
    {
        return binaryBlobKeys;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public static final class Builder
    {
        private NodeVersionId nodeVersionId;

        private NodeVersionKey nodeVersionKey;

        private BlobKeys binaryBlobKeys;

        private NodeId nodeId;

        private NodePath nodePath;

        private NodeCommitId nodeCommitId;

        private Instant timestamp;

        private Builder()
        {
        }

        private Builder( NodeVersionMetadata nodeVersionMetadata )
        {
            nodeVersionId = nodeVersionMetadata.nodeVersionId;
            nodeVersionKey = nodeVersionMetadata.nodeVersionKey;
            binaryBlobKeys = nodeVersionMetadata.binaryBlobKeys;
            nodeId = nodeVersionMetadata.nodeId;
            nodePath = nodeVersionMetadata.nodePath;
            nodeCommitId = nodeVersionMetadata.nodeCommitId;
            timestamp = nodeVersionMetadata.timestamp;
        }

        public Builder nodeVersionId( NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder nodeVersionKey( NodeVersionKey nodeVersionKey )
        {
            this.nodeVersionKey = nodeVersionKey;
            return this;
        }

        public Builder binaryBlobKeys( BlobKeys binaryBlobKeys )
        {
            this.binaryBlobKeys = binaryBlobKeys;
            return this;
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodePath( NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder nodeCommitId( NodeCommitId nodeCommitId )
        {
            this.nodeCommitId = nodeCommitId;
            return this;
        }

        public Builder timestamp( Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public NodeVersionMetadata build()
        {
            return new NodeVersionMetadata( this );
        }
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

        final NodeVersionMetadata that = (NodeVersionMetadata) o;

        if ( nodeVersionId != null ? !nodeVersionId.equals( that.nodeVersionId ) : that.nodeVersionId != null )
        {
            return false;
        }
        if ( nodeVersionKey != null ? !nodeVersionKey.equals( that.nodeVersionKey ) : that.nodeVersionKey != null )
        {
            return false;
        }
        if ( binaryBlobKeys != null ? !binaryBlobKeys.equals( that.binaryBlobKeys ) : that.binaryBlobKeys != null )
        {
            return false;
        }
        if ( nodeId != null ? !nodeId.equals( that.nodeId ) : that.nodeId != null )
        {
            return false;
        }
        if ( nodePath != null ? !nodePath.equals( that.nodePath ) : that.nodePath != null )
        {
            return false;
        }
        if ( nodeCommitId != null ? !nodeCommitId.equals( that.nodeCommitId ) : that.nodeCommitId != null )
        {
            return false;
        }
        return !( timestamp != null ? !timestamp.equals( that.timestamp ) : that.timestamp != null );

    }

    @Override
    public int hashCode()
    {
        int result = nodeVersionId != null ? nodeVersionId.hashCode() : 0;
        result = 31 * result + ( nodeVersionKey != null ? nodeVersionKey.hashCode() : 0 );
        result = 31 * result + ( binaryBlobKeys != null ? binaryBlobKeys.hashCode() : 0 );
        result = 31 * result + ( nodeId != null ? nodeId.hashCode() : 0 );
        result = 31 * result + ( nodePath != null ? nodePath.hashCode() : 0 );
        result = 31 * result + ( nodeCommitId != null ? nodeCommitId.hashCode() : 0 );
        result = 31 * result + ( timestamp != null ? timestamp.hashCode() : 0 );
        return result;
    }
}
