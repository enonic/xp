package com.enonic.xp.repository;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.BaseException;

public class ChildBranchFoundException
    extends BaseException
{
    public ChildBranchFoundException( final Branch branch, final Branch parentBranch )
    {
        super( "Branch with id [" + branch + "] and parent branch [" + parentBranch + "] found" );
    }

    public String getCode()
    {
        return "childBranchFound";
    }
}
