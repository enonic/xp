package com.enonic.xp.repo.impl.dump.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.node.NodeId;

public final class BranchDumpEntry
{
    private final NodeId nodeId;

    private final VersionMeta meta;

    private final ImmutableSet<String> binaryReferences;

    private BranchDumpEntry( final Builder builder )
    {
        nodeId = builder.nodeId;
        meta = builder.meta;
        binaryReferences = builder.binaryReferences.build();
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

        private ImmutableSet.Builder<String> binaryReferences = ImmutableSet.builder();

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
            this.binaryReferences = ImmutableSet.<String>builder().addAll( references  );
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
        final BranchDumpEntry that = (BranchDumpEntry) o;
        return Objects.equals( nodeId, that.nodeId ) && Objects.equals( meta, that.meta ) &&
            Objects.equals( binaryReferences, that.binaryReferences );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( nodeId, meta, binaryReferences );
    }
}
