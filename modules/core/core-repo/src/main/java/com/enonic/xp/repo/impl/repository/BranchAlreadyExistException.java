package com.enonic.xp.repo.impl.repository;


import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class BranchAlreadyExistException
    extends RuntimeException
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
}
