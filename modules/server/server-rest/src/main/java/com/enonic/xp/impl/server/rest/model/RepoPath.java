package com.enonic.xp.impl.server.rest.model;

import java.util.regex.Pattern;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public class RepoPath
{
    private static final String SEPARATOR = ":";

    private final Branch branch;

    private final RepositoryId repositoryId;

    private final NodePath nodePath;

    private RepoPath( final RepositoryId repositoryId, final Branch branch, final NodePath nodePath )
    {
        this.branch = branch;
        this.repositoryId = repositoryId;
        this.nodePath = nodePath;
    }

    public static RepoPath from( final String repoPath )
    {
        checkArgument( !isNullOrEmpty( repoPath ) );

        final String[] elements = repoPath.split( Pattern.quote( SEPARATOR ), -1 );

        checkArgument( elements.length == 3, "Not a valid repository path" );

        checkArgument( !isNullOrEmpty( elements[0] ), "repositoryId cannot be empty" );
        checkArgument( !isNullOrEmpty( elements[1] ), "Branch cannot be empty" );
        checkArgument( !isNullOrEmpty( elements[2] ), "nodePath cannot be empty" );

        return new RepoPath( RepositoryId.from( elements[0] ), Branch.from( elements[1] ), new NodePath( elements[2] ) );
    }

    @Override
    public String toString()
    {
        return repositoryId + SEPARATOR + branch + SEPARATOR + nodePath;
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
