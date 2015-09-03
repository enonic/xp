package com.enonic.xp.node;

import java.time.Instant;

import com.google.common.annotations.Beta;

@Beta
public class NodeVersion
    implements Comparable<NodeVersion>
{
    private final NodeVersionId nodeVersionId;

    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private NodeVersion( Builder builder )
    {
        nodeVersionId = builder.nodeVersionId;
        nodeId = builder.nodeId;
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
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

    // Insert with newest first
    @Override
    public int compareTo( final NodeVersion o )
    {
        if ( this.timestamp == o.timestamp )
        {
            return 0;
        }

        if ( this.timestamp.isBefore( o.timestamp ) )
        {
            return 1;
        }

        return -1;
    }

    public static final class Builder
    {
        private NodeVersionId nodeVersionId;

        private NodeId nodeId;

        private NodePath nodePath;

        private Instant timestamp;

        private Builder()
        {
        }

        public Builder nodeVersionId( NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
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

        public Builder timestamp( Instant timestamp )
        {
            this.timestamp = timestamp;
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
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final NodeVersion that = (NodeVersion) o;

        if ( nodeVersionId != null ? !nodeVersionId.equals( that.nodeVersionId ) : that.nodeVersionId != null )
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
        return !( timestamp != null ? !timestamp.equals( that.timestamp ) : that.timestamp != null );

    }

    @Override
    public int hashCode()
    {
        int result = nodeVersionId != null ? nodeVersionId.hashCode() : 0;
        result = 31 * result + ( nodeId != null ? nodeId.hashCode() : 0 );
        result = 31 * result + ( nodePath != null ? nodePath.hashCode() : 0 );
        result = 31 * result + ( timestamp != null ? timestamp.hashCode() : 0 );
        return result;
    }
}
