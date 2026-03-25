package com.enonic.xp.repo.impl.dump.model;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.node.NodeId;

public record BranchDumpEntry(NodeId nodeId, VersionMeta meta, List<String> binaryReferences)
{
    public BranchDumpEntry
    {
        Objects.requireNonNull( nodeId );
        Objects.requireNonNull( meta );
        binaryReferences = List.copyOf( binaryReferences );
    }
}
