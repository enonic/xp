package com.enonic.xp.repo.impl.storage;

import java.time.Instant;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;

public final class StoreNodeBranchParams
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeVersionKey nodeVersionKey;

    private final NodeVersionId nodeVersionId;

    private final NodePath previousPath;

    private StoreNodeBranchParams( final Builder builder )
    {
        nodeVersionKey = builder.nodeVersionKey;
        timestamp = builder.timestamp;
        nodePath = builder.nodePath;
        nodeId = builder.nodeId;
        nodeVersionId = builder.nodeVersionId;
        previousPath = builder.previousPath;
    }

    public static Builder create()
    {
        return new Builder();
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

    public NodeVersionKey getNodeVersionKey()
    {
        return nodeVersionKey;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public NodePath getPreviousPath()
    {
        return previousPath;
    }

    public static final class Builder
    {
        private NodeVersionKey nodeVersionKey;

        private Instant timestamp;

        private NodePath nodePath;

        private NodeId nodeId;

        private NodeVersionId nodeVersionId;

        private NodePath previousPath;


        private Builder()
        {
        }

        public Builder nodeVersionKey( final NodeVersionKey val )
        {
            nodeVersionKey = val;
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

        public Builder previousPath( final NodePath val )
        {
            previousPath = val;
            return this;
        }

        public StoreNodeBranchParams build()
        {
            return new StoreNodeBranchParams( this );
        }
    }
}
