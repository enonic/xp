package com.enonic.xp.repository;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.NotFoundException;

@PublicApi
public class BranchNotFoundException
    extends NotFoundException
{
    public BranchNotFoundException( final Branch branch )
    {
        super( "Branch with id [" + branch + "] not found" );
    }

    @Override
    public String getCode()
    {
        return "branchNotFound";
    }
}
