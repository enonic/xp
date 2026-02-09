package com.enonic.xp.node;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.blob.BlobKeys;

@PublicApi
public final class NodeVersion
{
    private final NodeVersionId nodeVersionId;

    private final NodeVersionKey nodeVersionKey;

    private final BlobKeys binaryBlobKeys;

    private final NodeId nodeId;

    private final NodePath nodePath;

    private final NodeCommitId nodeCommitId;

    private final Instant timestamp;

    private final Attributes attributes;

    private NodeVersion( Builder builder )
    {
        nodeVersionId = Objects.requireNonNull( builder.nodeVersionId );
        nodeVersionKey = Objects.requireNonNull( builder.nodeVersionKey );
        binaryBlobKeys = Objects.requireNonNull( builder.binaryBlobKeys );
        nodeId = Objects.requireNonNull( builder.nodeId );
        nodePath = Objects.requireNonNull( builder.nodePath );
        nodeCommitId = builder.nodeCommitId;
        timestamp = Objects.requireNonNull( builder.timestamp );
        attributes = builder.attributes;
    }

    public static Builder create()
    {
        return new Builder();
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

    public Attributes getAttributes()
    {
        return attributes;
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

        private Attributes attributes;

        private Builder()
        {
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

        public Builder attributes( Attributes attributes )
        {
            this.attributes = attributes;
            return this;
        }

        public NodeVersion build()
        {
            return new NodeVersion( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        return o instanceof final NodeVersion that && Objects.equals( nodeVersionId, that.nodeVersionId ) &&
            Objects.equals( nodeVersionKey, that.nodeVersionKey ) && Objects.equals( binaryBlobKeys, that.binaryBlobKeys ) &&
            Objects.equals( nodeId, that.nodeId ) && Objects.equals( nodePath, that.nodePath ) &&
            Objects.equals( nodeCommitId, that.nodeCommitId ) && Objects.equals( timestamp, that.timestamp ) &&
            Objects.equals( attributes, that.attributes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( nodeVersionId, nodeVersionKey, binaryBlobKeys, nodeId, nodePath, nodeCommitId, timestamp, attributes );
    }
}
