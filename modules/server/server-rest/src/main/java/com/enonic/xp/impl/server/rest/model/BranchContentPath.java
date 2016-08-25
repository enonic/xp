package com.enonic.xp.impl.server.rest.model;


import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;

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
        Preconditions.checkArgument( !Strings.isNullOrEmpty( branch ), "Branch cannot be empty" );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( contentPath ), "ContentPath cannot be empty" );

        return new BranchContentPath( Branch.from( branch ), ContentPath.from( contentPath ) );
    }

    public static BranchContentPath from( final String repoPath )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( repoPath ) );

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
