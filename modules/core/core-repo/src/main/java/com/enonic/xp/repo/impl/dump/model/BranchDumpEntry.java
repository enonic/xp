package com.enonic.xp.repo.impl.dump.model;

import java.util.List;

import com.enonic.xp.node.NodeId;

public record BranchDumpEntry(NodeId nodeId, VersionMeta meta, List<String> binaryReferences)
{
}
