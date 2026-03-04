package com.enonic.xp.repository;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class DeleteBranchParams
{

    private final Branch branch;

    private DeleteBranchParams( final Branch branch )
    {
        this.branch = branch;
    }

    public static DeleteBranchParams from( final String branch )
    {
        return new DeleteBranchParams( Branch.from( branch ) );
    }

    public static DeleteBranchParams from( final Branch branch )
    {
        return new DeleteBranchParams( branch );
    }

    public Branch getBranch()
    {
        return branch;
    }
}


