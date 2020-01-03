package com.enonic.xp.impl.server.rest.model;


import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;

import static com.google.common.base.Strings.isNullOrEmpty;

public class BranchContentPath
{
    private final static String SEPARATOR = ":";

    private final Branch branch;

    private final ContentPath contentPath;

    private BranchContentPath( final Branch branch, final ContentPath contentPath )
    {
        this.branch = branch;
        this.contentPath = contentPath;
    }

    private static BranchContentPath from( final String branch, final String contentPath )
    {
        Preconditions.checkArgument( !isNullOrEmpty( branch ), "Branch cannot be empty" );
        Preconditions.checkArgument( !isNullOrEmpty( contentPath ), "ContentPath cannot be empty" );

        return new BranchContentPath( Branch.from( branch ), ContentPath.from( contentPath ) );
    }

    public static BranchContentPath from( final String repoPath )
    {
        Preconditions.checkArgument( !isNullOrEmpty( repoPath ) );

        final String[] elements = repoPath.split( Pattern.quote( SEPARATOR ) );

        Preconditions.checkArgument( elements.length == 2, "Not a valid branch content path" );

        return BranchContentPath.from( elements[0], elements[1] );
    }

    public Branch getBranch()
    {
        return branch;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }
}
