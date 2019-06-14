package com.enonic.xp.branch;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class BranchInfos
    extends AbstractImmutableEntitySet<BranchInfo>
{
    protected final Branches branches;

    private BranchInfos( final ImmutableSet<BranchInfo> set )
    {
        super( set );
        final List<Branch> branches = set.stream().
            map( BranchInfo::getBranch ).
            collect( Collectors.toList() );
        this.branches = Branches.from( branches );
    }

    public static BranchInfos from( final BranchInfo... brancheInfos )
    {
        return new BranchInfos( ImmutableSet.copyOf( brancheInfos ) );
    }

    public static BranchInfos from( final Iterable<BranchInfo> brancheInfos )
    {
        return new BranchInfos( ImmutableSet.copyOf( brancheInfos ) );
    }

    public static BranchInfos empty()
    {
        return new BranchInfos( ImmutableSet.of() );
    }

    public Branches getBranches()
    {
        return branches;
    }
}
