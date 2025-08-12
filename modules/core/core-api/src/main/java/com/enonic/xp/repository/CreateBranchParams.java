package com.enonic.xp.repository;

import com.enonic.xp.branch.Branch;

public final class CreateBranchParams
{
    private final Branch branch;

    private CreateBranchParams( final Branch branch )
    {
        this.branch = branch;
    }

    public static CreateBranchParams from( final String branchId )
    {
        return new CreateBranchParams( Branch.from( branchId ) );
    }

    public static CreateBranchParams from( final Branch branch )
    {
        return new CreateBranchParams( branch );
    }


    public Branch getBranch()
    {
        return branch;
    }
}


