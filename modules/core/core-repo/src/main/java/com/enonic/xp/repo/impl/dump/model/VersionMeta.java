package com.enonic.xp.repo.impl.dump.model;

import java.time.Instant;

import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;

public class VersionMeta
{
    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeVersionId version;

    private final NodeVersionKey nodeVersionKey;

    private final NodeCommitId nodeCommitId;

    private VersionMeta( final Builder builder )
    {
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        version = builder.version;
        nodeVersionKey = builder.nodeVersionKey;
        nodeCommitId = builder.nodeCommitId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public NodeVersionId getVersion()
    {
        return version;
    }

    public NodeVersionKey getNodeVersionKey()
    {
        return nodeVersionKey;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePath nodePath;

        private Instant timestamp;

        private NodeVersionId version;

        private NodeVersionKey nodeVersionKey;

        private NodeCommitId nodeCommitId;

        private Builder()
        {
        }

        public Builder nodePath( final NodePath val )
        {
            nodePath = val;
            return this;
        }

        public Builder timestamp( final Instant val )
        {
            timestamp = val;
            return this;
        }

        public Builder version( final NodeVersionId val )
        {
            version = val;
            return this;
        }

        public Builder nodeVersionKey( final NodeVersionKey val )
        {
            nodeVersionKey = val;
            return this;
        }

        public Builder nodeCommitId( final NodeCommitId val )
        {
            nodeCommitId = val;
            return this;
        }

        public VersionMeta build()
        {
            return new VersionMeta( this );
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

        final VersionMeta meta = (VersionMeta) o;

        if ( nodePath != null ? !nodePath.equals( meta.nodePath ) : meta.nodePath != null )
        {
            return false;
        }
        if ( timestamp != null ? !timestamp.equals( meta.timestamp ) : meta.timestamp != null )
        {
            return false;
        }
        if ( version != null ? !version.equals( meta.version ) : meta.version != null )
        {
            return false;
        }
        if ( nodeVersionKey != null ? !nodeVersionKey.equals( meta.nodeVersionKey ) : meta.nodeVersionKey != null )
        {
            return false;
        }
        return nodeCommitId != null ? nodeCommitId.equals( meta.nodeCommitId ) : meta.nodeCommitId == null;

    }

    @Override
    public int hashCode()
    {
        int result = nodePath != null ? nodePath.hashCode() : 0;
        result = 31 * result + ( timestamp != null ? timestamp.hashCode() : 0 );
        result = 31 * result + ( version != null ? version.hashCode() : 0 );
        result = 31 * result + ( nodeVersionKey != null ? nodeVersionKey.hashCode() : 0 );
        result = 31 * result + ( nodeCommitId != null ? nodeCommitId.hashCode() : 0 );
        return result;
    }
}
