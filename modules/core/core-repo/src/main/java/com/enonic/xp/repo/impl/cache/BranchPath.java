package com.enonic.xp.repo.impl.cache;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;

public class BranchPath
    implements CachePath
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final NodePath path;

    public BranchPath( final RepositoryId repositoryId, final Branch branch, final NodePath path )
    {
        this.repositoryId = repositoryId;
        this.branch = branch;
        this.path = path;
    }

    @Override
    public CachePath getParentPath()
    {
        return new BranchPath( this.repositoryId, this.branch, path.getParentPath() );
    }

    @Override
    public String toString()
    {
        return ( repositoryId == null ? null : repositoryId.toString() ) + ":" + ( branch == null ? null : branch.getValue() ) + ":" + path;
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

        if ( repositoryId != null ? !repositoryId.equals( that.repositoryId ) : that.repositoryId != null )
        {
            return false;
        }
        if ( branch != null ? !branch.equals( that.branch ) : that.branch != null )
        {
            return false;
        }
        return !( path != null ? !path.equals( that.path ) : that.path != null );
    }

    @Override
    public int hashCode()
    {
        int result = repositoryId != null ? repositoryId.hashCode() : 0;
        result = 31 * result + ( branch != null ? branch.hashCode() : 0 );
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        return result;
    }
}
