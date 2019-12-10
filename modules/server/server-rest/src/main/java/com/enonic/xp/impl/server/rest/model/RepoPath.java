package com.enonic.xp.impl.server.rest.model;

import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RepoPath
{
    private final static String SEPARATOR = ":";

    private final Branch branch;

    private final RepositoryId repositoryId;

    private final NodePath nodePath;

    private RepoPath( final Branch branch, final RepositoryId repositoryId, final NodePath nodePath )
    {
        this.branch = branch;
        this.repositoryId = repositoryId;
        this.nodePath = nodePath;
    }

    private static RepoPath from( final String repositoryId, final String branch, final String nodePath )
    {
        Preconditions.checkArgument( !isNullOrEmpty( branch ), "Branch cannot be empty" );
        Preconditions.checkArgument( !isNullOrEmpty( repositoryId ), "repositoryId cannot be empty" );
        Preconditions.checkArgument( !isNullOrEmpty( nodePath ), "nodePath cannot be empty" );

        return new RepoPath( Branch.from( branch ), RepositoryId.from( repositoryId ), NodePath.create( nodePath ).build() );
    }

    public static RepoPath from( final String repoPath )
    {
        Preconditions.checkArgument( !isNullOrEmpty( repoPath ) );

        final String[] elements = repoPath.split( Pattern.quote( SEPARATOR ) );

        Preconditions.checkArgument( elements.length == 3, "Not a valid repository path" );

        return RepoPath.from( elements[0], elements[1], elements[2] );
    }

    public Branch getBranch()
    {
        return branch;
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
