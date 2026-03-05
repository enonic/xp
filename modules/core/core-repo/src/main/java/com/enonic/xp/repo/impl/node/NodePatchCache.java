package com.enonic.xp.repo.impl.node;

import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;

final class NodePatchCache<T>
{
    private final Map<NodeVersionId, Entry<T>> cache = new HashMap<>();

    Entry<T> get( final NodeVersionId nodeVersionId )
    {
        return cache.get( nodeVersionId );
    }

    void put( final NodeVersionId nodeVersionId, final Branch originBranch, final NodeVersion newVersion, final T newData )
    {
        cache.put( nodeVersionId, new Entry<>( newData, newVersion, originBranch ) );
    }

    record Entry<T>(T data, NodeVersion version, Branch originBranch)
    {
    }
}