package com.enonic.xp.node;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;

@PublicApi
public class NodeVersionMetadata
    implements Comparable<NodeVersionMetadata>
{
    private final NodeVersionId nodeVersionId;

    private final NodeVersionKey nodeVersionKey;

    private final BlobKeys binaryBlobKeys;

    private final NodeId nodeId;

    private final NodePath nodePath;

    private final NodeCommitId nodeCommitId;

    private final Instant timestamp;

    private final Branches branches;

    private NodeVersionMetadata( Builder builder )
    {
        nodeVersionId = builder.nodeVersionId;
        nodeVersionKey = builder.nodeVersionKey;
        binaryBlobKeys = builder.binaryBlobKeys;
        nodeId = builder.nodeId;
        nodePath = builder.nodePath;
        nodeCommitId = builder.nodeCommitId;
        timestamp = builder.timestamp;
        branches = Branches.from( builder.branches.build() );
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

    public Branches getBranches()
    {
        return branches;
    }

    // Insert with newest first
    @Override
    public int compareTo( final NodeVersionMetadata o )
    {
        if ( this.timestamp.equals( o.timestamp ) )
        {
            return 0;
        }

        if ( this.timestamp.isBefore( o.timestamp ) )
        {
            return 1;
        }

        return -1;
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
        if ( !Objects.equals( branches, that.branches ) )
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
        result = 31 * result + ( branches != null ? branches.hashCode() : 0 );
        return result;
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

        private ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();

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
            branches = branches.addAll( nodeVersionMetadata.branches );
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

        public Builder setBranches( Branches branches )
        {
            if ( branches != null )
            {
                this.branches = ImmutableSet.<Branch>builder().addAll( branches );
            }
            return this;
        }

        public NodeVersionMetadata build()
        {
            return new NodeVersionMetadata( this );
        }
    }
}
