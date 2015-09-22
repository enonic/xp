package com.enonic.wem.repo.internal.storage.branch;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public class BranchNodeVersions
    extends AbstractImmutableEntityList<BranchNodeVersion>
{
    private BranchNodeVersions( final ImmutableList<BranchNodeVersion> list )
    {
        super( list );
    }

    public static BranchNodeVersions from( final Collection<BranchNodeVersion> branchNodeVersions )
    {
        return new BranchNodeVersions( ImmutableList.copyOf( branchNodeVersions ) );
    }

}
