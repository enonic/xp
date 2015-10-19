package com.enonic.xp.repo.impl.branch.storage;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.support.AbstractImmutableEntityList;

public class BranchNodeVersions
    extends AbstractImmutableEntityList<NodeBranchMetadata>
{
    private Map<NodeId, NodeBranchMetadata> branchNodeVersionMap = Maps.newHashMap();

    private BranchNodeVersions( final ImmutableList<NodeBranchMetadata> list )
    {
        super( list );

        for ( final NodeBranchMetadata nodeBranchMetadata : list )
        {
            branchNodeVersionMap.put( nodeBranchMetadata.getNodeId(), nodeBranchMetadata );
        }
    }

    public NodeBranchMetadata get( final NodeId nodeId )
    {
        return branchNodeVersionMap.get( nodeId );
    }

    public static BranchNodeVersions from( final Collection<NodeBranchMetadata> nodeBranchMetadatas )
    {
        return new BranchNodeVersions( ImmutableList.copyOf( nodeBranchMetadatas ) );
    }

}