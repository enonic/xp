package com.enonic.xp.repo.impl.repository;


import java.text.MessageFormat;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.BaseException;

class BranchAlreadyExistException
    extends BaseException
{
    private final Branch branch;

    BranchAlreadyExistException( final Branch branch )
    {
        super( MessageFormat.format( "Branch [{0}] already exists", branch ) );
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
