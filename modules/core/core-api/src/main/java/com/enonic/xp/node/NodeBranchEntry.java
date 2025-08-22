package com.enonic.xp.node;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.blob.NodeVersionKey;

public final class NodeBranchEntry
{
    private final NodeVersionId nodeVersionId;

    private final NodeVersionKey nodeVersionKey;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeId nodeId;

    private NodeBranchEntry( Builder builder )
    {
        this.nodeVersionId = builder.nodeVersionId;
        this.nodeVersionKey = builder.nodeVersionKey;
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

    public NodeVersionKey getNodeVersionKey()
    {
        return nodeVersionKey;
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

        final NodeBranchEntry that = (NodeBranchEntry) o;

        if ( nodeVersionId != null ? !nodeVersionId.equals( that.nodeVersionId ) : that.nodeVersionId != null )
        {
            return false;
        }
        if ( nodeVersionKey != null ? !nodeVersionKey.equals( that.nodeVersionKey ) : that.nodeVersionKey != null )
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
        result = 31 * result + ( nodeVersionKey != null ? nodeVersionKey.hashCode() : 0 );
        result = 31 * result + ( nodePath != null ? nodePath.hashCode() : 0 );
        result = 31 * result + ( timestamp != null ? timestamp.hashCode() : 0 );
        result = 31 * result + ( nodeId != null ? nodeId.hashCode() : 0 );
        return result;
    }

    public static final class Builder
    {
        private NodeVersionId nodeVersionId;

        private NodeVersionKey nodeVersionKey;

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

        public Builder nodeVersionKey( final NodeVersionKey nodeVersionKey )
        {
            this.nodeVersionKey = nodeVersionKey;
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
            Objects.requireNonNull( this.nodePath, "nodePath is required" );
            Objects.requireNonNull( this.nodeId, "nodeId is required" );
        }

        public NodeBranchEntry build()
        {
            validate();
            return new NodeBranchEntry( this );
        }
    }
}
