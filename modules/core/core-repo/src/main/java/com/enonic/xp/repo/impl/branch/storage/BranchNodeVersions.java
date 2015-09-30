package com.enonic.xp.repo.impl.branch.storage;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.support.AbstractImmutableEntityList;

public class BranchNodeVersions
    extends AbstractImmutableEntityList<BranchNodeVersion>
{
    private Map<NodeId, BranchNodeVersion> branchNodeVersionMap = Maps.newHashMap();

    private BranchNodeVersions( final ImmutableList<BranchNodeVersion> list )
    {
        super( list );

        for ( final BranchNodeVersion branchNodeVersion : list )
        {
            branchNodeVersionMap.put( branchNodeVersion.getNodeId(), branchNodeVersion );
        }
    }

    public BranchNodeVersion get( final NodeId nodeId )
    {
        return branchNodeVersionMap.get( nodeId );
    }

    public static BranchNodeVersions from( final Collection<BranchNodeVersion> branchNodeVersions )
    {
        return new BranchNodeVersions( ImmutableList.copyOf( branchNodeVersions ) );
    }

}