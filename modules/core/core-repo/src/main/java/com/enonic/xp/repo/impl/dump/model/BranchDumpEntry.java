package com.enonic.xp.repo.impl.dump.model;

import java.util.Collection;
import java.util.HashSet;

import com.enonic.xp.node.NodeId;

public class BranchDumpEntry
{
    private final NodeId nodeId;

    private final VersionMeta meta;

    private final Collection<String> binaryReferences;

    private BranchDumpEntry( final Builder builder )
    {
        nodeId = builder.nodeId;
        meta = builder.meta;
        binaryReferences = builder.binaryReferences;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Collection<String> getBinaryReferences()
    {
        return binaryReferences;
    }

    public VersionMeta getMeta()
    {
        return meta;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private Collection<String> binaryReferences = new HashSet<>();

        private VersionMeta meta;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder meta( final VersionMeta meta )
        {
            this.meta = meta;
            return this;
        }

        public Builder setBinaryReferences( final Collection<String> references )
        {
            this.binaryReferences = references;
            return this;
        }

        public Builder addBinaryReferences( final Collection<String> references )
        {
            this.binaryReferences.addAll( references );
            return this;
        }

        public BranchDumpEntry build()
        {
            return new BranchDumpEntry( this );
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

        final BranchDumpEntry branchDumpEntry = (BranchDumpEntry) o;

        if ( nodeId != null ? !nodeId.equals( branchDumpEntry.nodeId ) : branchDumpEntry.nodeId != null )
        {
            return false;
        }

        return binaryReferences != null ? binaryReferences.equals( branchDumpEntry.binaryReferences ) : branchDumpEntry.binaryReferences == null;

    }

    @Override
    public int hashCode()
    {
        int result = nodeId != null ? nodeId.hashCode() : 0;
        result = 31 * result + ( binaryReferences != null ? binaryReferences.hashCode() : 0 );
        return result;
    }
}
