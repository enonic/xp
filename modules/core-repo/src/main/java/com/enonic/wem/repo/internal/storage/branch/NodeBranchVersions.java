package com.enonic.wem.repo.internal.storage.branch;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public class NodeBranchVersions
    extends AbstractImmutableEntitySet<NodeBranchVersion>
{
    public NodeBranchVersions( final ImmutableSet<NodeBranchVersion> set )
    {
        super( set );
    }

    public static NodeBranchVersions from( final NodeBranchVersion... nodeBranchVersions )
    {
        return new NodeBranchVersions( ImmutableSet.copyOf( nodeBranchVersions ) );
    }

    public static NodeBranchVersions from( final Collection<NodeBranchVersion> nodeBranchVersions )
    {
        return new NodeBranchVersions( ImmutableSet.copyOf( nodeBranchVersions ) );
    }

    public static NodeBranchVersions empty()
    {
        ImmutableSet<NodeBranchVersion> empty = ImmutableSet.of();
        return new NodeBranchVersions( empty );
    }


}
