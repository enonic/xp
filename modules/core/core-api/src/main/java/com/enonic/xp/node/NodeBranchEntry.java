package com.enonic.xp.node;

import java.time.Instant;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.blob.NodeVersionKey;

public class NodeBranchEntry
{
    private final NodeVersionId nodeVersionId;

    private final NodeVersionKey nodeVersionKey;

    private final NodeState nodeState;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeId nodeId;

    private final boolean inherited;

    private NodeBranchEntry( Builder builder )
    {
        this.nodeVersionId = builder.nodeVersionId;
        this.nodeVersionKey = builder.nodeVersionKey;
        this.nodeState = builder.state;
        this.nodePath = builder.nodePath;
        this.timestamp = builder.timestamp;
        this.nodeId = builder.nodeId;
        this.inherited = builder.inherited;
    }

    public NodeVersionId getVersionId()
    {
        return nodeVersionId;
    }

    public NodeVersionKey getNodeVersionKey()
    {
        return nodeVersionKey;
    }

    public NodeState getNodeState()
    {
        return nodeState;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public boolean isInherited()
    {
        return inherited;
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
        final NodeBranchEntry that = (NodeBranchEntry) o;
        return inherited == that.inherited && Objects.equals( nodeVersionId, that.nodeVersionId ) &&
            Objects.equals( nodeVersionKey, that.nodeVersionKey ) && nodeState == that.nodeState &&
            Objects.equals( nodePath, that.nodePath ) && Objects.equals( timestamp, that.timestamp ) &&
            Objects.equals( nodeId, that.nodeId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( nodeVersionId, nodeVersionKey, nodeState, nodePath, timestamp, nodeId, inherited );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( NodeBranchEntry nodeBranchEntry )
    {
        return new Builder( nodeBranchEntry );
    }

    public static final class Builder
    {
        private NodeVersionId nodeVersionId;

        private NodeVersionKey nodeVersionKey;

        private NodeState state;

        private NodePath nodePath;

        private Instant timestamp;

        private NodeId nodeId;

        private boolean inherited;

        private Builder()
        {
        }

        private Builder( NodeBranchEntry nodeBranchEntry )
        {
            nodeVersionId = nodeBranchEntry.nodeVersionId;
            nodeVersionKey = nodeBranchEntry.nodeVersionKey;
            state = nodeBranchEntry.nodeState;
            nodePath = nodeBranchEntry.nodePath;
            timestamp = nodeBranchEntry.timestamp;
            nodeId = nodeBranchEntry.nodeId;
            inherited = nodeBranchEntry.inherited;
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder nodeVersionKey( final NodeVersionKey nodeVersionKey )
        {
            this.nodeVersionKey = nodeVersionKey;
            return this;
        }

        public Builder nodeState( final NodeState state )
        {
            this.state = state;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder inherited( final boolean inherited )
        {
            this.inherited = inherited;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.nodePath, "NodePath must be set" );
            Preconditions.checkNotNull( this.nodeId, "NodeId must be set" );
            Preconditions.checkNotNull( this.state, "Nodestate must be set" );
        }

        public NodeBranchEntry build()
        {
            validate();
            return new NodeBranchEntry( this );
        }
    }
}
