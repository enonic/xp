package com.enonic.xp.repo.impl.dump.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.enonic.xp.node.NodeId;

public record VersionsDumpEntry(NodeId nodeId, List<VersionMeta> versions)
{
    public VersionsDumpEntry
    {
        Objects.requireNonNull( nodeId );
        versions = List.copyOf( versions );
    }
}
