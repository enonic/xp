package com.enonic.xp.impl.server.rest.model;

import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;

public class RepoPath
{
    private final static String SEPARATOR = ":";

    private final BranchId branchId;

    private final RepositoryId repositoryId;

    private final NodePath nodePath;

    private RepoPath( final BranchId branchId, final RepositoryId repositoryId, final NodePath nodePath )
    {
        this.branchId = branchId;
        this.repositoryId = repositoryId;
        this.nodePath = nodePath;
    }

    private static RepoPath from( final String repositoryId, final String branch, final String nodePath )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( branch ), "Branch cannot be empty" );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( repositoryId ), "repositoryId cannot be empty" );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( nodePath ), "nodePath cannot be empty" );

        return new RepoPath( BranchId.from( branch ), RepositoryId.from( repositoryId ), NodePath.create( nodePath ).build() );
    }

    public static RepoPath from( final String repoPath )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( repoPath ) );

        final String[] elements = repoPath.split( Pattern.quote( SEPARATOR ) );

        Preconditions.checkArgument( elements.length == 3, "Not a valid repository path" );

        return RepoPath.from( elements[0], elements[1], elements[2] );
    }

    public BranchId getBranchId()
    {
        return branchId;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }
}
