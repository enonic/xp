package com.enonic.xp.repository;


import java.text.MessageFormat;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.DuplicateElementException;

public final class BranchAlreadyExistsException
    extends DuplicateElementException
{
    private final Branch branch;

    public BranchAlreadyExistsException( final Branch branch )
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
