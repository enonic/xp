package com.enonic.xp.repo.impl.cache;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;

public final class BranchPath
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final NodePath path;

    public BranchPath( final RepositoryId repositoryId, final Branch branch, final NodePath path )
    {
        this.repositoryId = Objects.requireNonNull( repositoryId );
        this.branch = Objects.requireNonNull( branch );
        this.path = Objects.requireNonNull( path );
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public NodePath getPath()
    {
        return path;
    }

    @Override
    public String toString()
    {
        return repositoryId + ":" + branch + ":" + path;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final BranchPath that = (BranchPath) o;
        return repositoryId.equals( that.repositoryId ) && branch.equals( that.branch ) && path.equals( that.path );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( repositoryId, branch, path );
    }
}
