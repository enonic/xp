package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;

public class NodeBranchMetadata
{
    private final NodeVersionId nodeVersionId;

    private final NodeState nodeState;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeId nodeId;

    private NodeBranchMetadata( Builder builder )
    {
        this.nodeVersionId = builder.nodeVersionId;
        this.nodeState = builder.state;
        this.nodePath = builder.nodePath;
        this.timestamp = builder.timestamp;
        this.nodeId = builder.nodeId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionId getVersionId()
    {
        return nodeVersionId;
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

        final NodeBranchMetadata that = (NodeBranchMetadata) o;

        if ( nodeVersionId != null ? !nodeVersionId.equals( that.nodeVersionId ) : that.nodeVersionId != null )
        {
            return false;
        }
        if ( nodeState != that.nodeState )
        {
            return false;
        }
        if ( nodePath != null ? !nodePath.equals( that.nodePath ) : that.nodePath != null )
        {
            return false;
        }
        if ( timestamp != null ? !timestamp.equals( that.timestamp ) : that.timestamp != null )
        {
            return false;
        }
        return !( nodeId != null ? !nodeId.equals( that.nodeId ) : that.nodeId != null );

    }

    @Override
    public int hashCode()
    {
        int result = nodeVersionId != null ? nodeVersionId.hashCode() : 0;
        result = 31 * result + ( nodeState != null ? nodeState.hashCode() : 0 );
        result = 31 * result + ( nodePath != null ? nodePath.hashCode() : 0 );
        result = 31 * result + ( timestamp != null ? timestamp.hashCode() : 0 );
        result = 31 * result + ( nodeId != null ? nodeId.hashCode() : 0 );
        return result;
    }

    public static final class Builder
    {
        private NodeVersionId nodeVersionId;

        private NodeState state;

        private NodePath nodePath;

        private Instant timestamp;

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
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

        private void validate()
        {
            Preconditions.checkNotNull( this.nodePath, "NodePath must be set" );
            Preconditions.checkNotNull( this.nodeId, "NodeId must be set" );
            Preconditions.checkNotNull( this.state, "Nodestate must be set" );
        }

        public NodeBranchMetadata build()
        {
            validate();
            return new NodeBranchMetadata( this );
        }
    }
}
