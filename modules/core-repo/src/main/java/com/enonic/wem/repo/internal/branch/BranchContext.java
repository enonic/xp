package com.enonic.wem.repo.internal.branch;

import com.enonic.xp.core.context.Context;
import com.enonic.xp.core.repository.RepositoryId;
import com.enonic.xp.core.branch.Branch;

public class BranchContext
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private BranchContext( final RepositoryId repositoryId, final Branch branch )
    {
        this.repositoryId = repositoryId;
        this.branch = branch;
    }

    public static BranchContext from( final Context context )
    {
        return new BranchContext( context.getRepositoryId(), context.getBranch() );
    }


    public static BranchContext from( final Branch branch, final RepositoryId repositoryId )
    {
        return new BranchContext( repositoryId, branch );
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof BranchContext ) )
        {
            return false;
        }

        final BranchContext that = (BranchContext) o;

        if ( repositoryId != null ? !repositoryId.equals( that.repositoryId ) : that.repositoryId != null )
        {
            return false;
        }
        if ( branch != null ? !branch.equals( that.branch ) : that.branch != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = repositoryId != null ? repositoryId.hashCode() : 0;
        result = 31 * result + ( branch != null ? branch.hashCode() : 0 );
        return result;
    }
}
