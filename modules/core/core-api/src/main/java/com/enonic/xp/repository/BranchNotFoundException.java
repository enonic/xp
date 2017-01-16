package com.enonic.xp.repository;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.BaseException;

public class BranchNotFoundException
    extends BaseException
{
    public BranchNotFoundException( final Branch branch )
    {
        super( "Branch with id [" + branch + "] not found" );
    }

    public String getCode()
    {
        return "branchNotFound";
    }
}
