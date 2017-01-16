package com.enonic.xp.repository;

import com.enonic.xp.branch.Branch;

public class DeleteBranchParams
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


