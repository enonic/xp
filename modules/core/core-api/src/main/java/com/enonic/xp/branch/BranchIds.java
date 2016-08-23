package com.enonic.xp.branch;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class BranchIds
    extends AbstractImmutableEntitySet<BranchId>
{
    private BranchIds( final ImmutableSet<BranchId> set )
    {
        super( set );
    }

    public static BranchIds from( final BranchId... branchIds )
    {
        return new BranchIds( ImmutableSet.copyOf( branchIds ) );
    }

    public static BranchIds from( final Iterable<BranchId> branches )
    {
        return new BranchIds( ImmutableSet.copyOf( branches ) );
    }

    public static BranchIds empty()
    {
        return new BranchIds( ImmutableSet.of() );
    }
}
