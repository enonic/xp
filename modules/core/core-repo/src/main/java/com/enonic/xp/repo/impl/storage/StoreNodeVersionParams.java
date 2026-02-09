package com.enonic.xp.repo.impl.storage;

import java.time.Instant;

import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Attributes;

public final class StoreNodeVersionParams
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeStoreVersion nodeVersion;

    private final NodeVersionId nodeVersionId;

    private final NodeCommitId nodeCommitId;

    private final Attributes attributes;

    private StoreNodeVersionParams( final Builder builder )
    {
        nodeVersion = builder.nodeVersion;
        timestamp = builder.timestamp;
        nodePath = builder.nodePath;
        nodeId = builder.nodeId;
        nodeVersionId = builder.nodeVersionId;
        nodeCommitId = builder.nodeCommitId;
        attributes = builder.attributes;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public NodeStoreVersion getNodeVersion()
    {
        return nodeVersion;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public Attributes getAttributes()
    {
        return attributes;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Attributes attributes;

        private NodeStoreVersion nodeVersion;

        private Instant timestamp;

        private NodePath nodePath;

        private NodeId nodeId;

        private NodeVersionId nodeVersionId;

        private NodeCommitId nodeCommitId;

        private Builder()
        {
        }

        public Builder nodeVersion( final NodeStoreVersion val )
        {
            nodeVersion = val;
            return this;
        }

        public Builder timestamp( final Instant val )
        {
            timestamp = val;
            return this;
        }

        public Builder nodePath( final NodePath val )
        {
            nodePath = val;
            return this;
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId val )
        {
            nodeVersionId = val;
            return this;
        }

        public Builder nodeCommitId( final NodeCommitId val )
        {
            nodeCommitId = val;
            return this;
        }

        public Builder attributes( final Attributes val )
        {
            attributes = val;
            return this;
        }

        public StoreNodeVersionParams build()
        {
            return new StoreNodeVersionParams( this );
        }
    }
}
