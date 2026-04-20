package com.enonic.xp.repo.impl.dump.model;

import java.util.List;
import java.util.Set;

import com.enonic.xp.node.NodeId;

import static java.util.Objects.requireNonNull;

public record VersionsDumpEntry(NodeId nodeId, List<VersionMeta> versions)
{
    public VersionsDumpEntry
    {
        requireNonNull( nodeId );
        versions = List.copyOf( versions );
    }
}
