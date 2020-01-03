package com.enonic.xp.repo.impl.repository;


import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.BaseException;

class BranchAlreadyExistException
    extends BaseException
{
    private final Branch branch;

    public BranchAlreadyExistException( final Branch branch )
    {
        super( "Branch [{" + branch.getValue() + "}] already exists" );
        this.branch = branch;
    }

    public Branch getBranch()
    {
        return branch;
    }

    @Override
    public String getCode()
    {
        return "branchAlreadyExists";
    }
}
