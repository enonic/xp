package com.enonic.xp.repo.impl.dump.model;

import java.util.List;
import java.util.Set;

import com.enonic.xp.node.NodeId;

public record VersionsDumpEntry(NodeId nodeId, List<VersionMeta> versions)
{
}
