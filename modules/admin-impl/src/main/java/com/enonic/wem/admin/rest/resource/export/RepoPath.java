package com.enonic.wem.admin.rest.resource.export;

import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.repository.RepositoryId;

class RepoPath
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
        Preconditions.checkArgument( !Strings.isNullOrEmpty( branch ), "Branch cannot be empty" );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( repositoryId ), "repositoryId cannot be empty" );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( nodePath ), "nodePath cannot be empty" );

        return new RepoPath( Branch.from( branch ), RepositoryId.from( repositoryId ), NodePath.newPath( nodePath ).build() );
    }

    static RepoPath from( final String repoPath )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( repoPath ) );

        final String[] elements = repoPath.split( Pattern.quote( SEPARATOR ) );

        Preconditions.checkArgument( elements.length == 3, "Not a valid repository path" );

        return RepoPath.from( elements[0], elements[1], elements[2] );
    }

    Branch getBranch()
    {
        return branch;
    }

    RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    NodePath getNodePath()
    {
        return nodePath;
    }
}
