package com.enonic.xp.repository;

import com.enonic.xp.branch.Branch;

public class CreateBranchParams
{
    private final Branch branch;

    private CreateBranchParams( final String branchId )
    {
        this.branch = Branch.from( branchId );
    }

    public static CreateBranchParams from( final String branchId )
    {
        return new CreateBranchParams( branchId );
    }

    public Branch getBranch()
    {
        return branch;
    }
}


