package com.enonic.xp.repo.impl.dump.model;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.node.NodeId;

public record BranchDumpEntry(NodeId nodeId, VersionMeta meta, Collection<String> binaryReferences)
{
    public BranchDumpEntry
    {
        binaryReferences = binaryReferences != null ? ImmutableSet.copyOf( binaryReferences ) : ImmutableSet.of();
    }

    public BranchDumpEntry( final NodeId nodeId, final VersionMeta meta )
    {
        this( nodeId, meta, null );
    }
}
