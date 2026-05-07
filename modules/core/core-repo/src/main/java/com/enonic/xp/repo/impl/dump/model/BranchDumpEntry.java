package com.enonic.xp.repo.impl.dump.model;

import java.util.List;
import com.enonic.xp.node.NodeId;

import static java.util.Objects.requireNonNull;

public record BranchDumpEntry(NodeId nodeId, VersionMeta meta, List<String> binaryReferences)
{
    public BranchDumpEntry
    {
        requireNonNull( nodeId );
        requireNonNull( meta );
        binaryReferences = List.copyOf( binaryReferences );
    }
}
