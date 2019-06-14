package com.enonic.xp.repository;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.BranchInfo;

public class CreateBranchParams
{
    private final BranchInfo branchInfo;

    public CreateBranchParams( final Branch branch )
    {
        this.branchInfo = BranchInfo.from( branch );
    }

    public CreateBranchParams( final Branch branch, final Branch parentBranch )
    {
        this.branchInfo = BranchInfo.from( branch );
    }

    public static CreateBranchParams from( final String branchId )
    {
        return new CreateBranchParams( Branch.from( branchId ) );
    }

    public static CreateBranchParams from( final Branch branch )
    {
        return new CreateBranchParams( branch );
    }

    public BranchInfo getBranchInfo()
    {
        return branchInfo;
    }

    public Branch getBranch()
    {
        return branchInfo.getBranch();
    }
}


