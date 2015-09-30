package com.enonic.wem.repo.internal.cache;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;

public class BranchPath
    implements CachePath
{
    private final Branch branch;

    private final NodePath path;

    public BranchPath( final Branch branch, final NodePath path )
    {
        this.branch = branch;
        this.path = path;
    }

    @Override
    public CachePath getParentPath()
    {
        return new BranchPath( this.branch, path.getParentPath() );
    }

    @Override
    public String toString()
    {
        return branch.getName() + ":" + path.toString();
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

        if ( branch != null ? !branch.equals( that.branch ) : that.branch != null )
        {
            return false;
        }
        return !( path != null ? !path.equals( that.path ) : that.path != null );

    }

    @Override
    public int hashCode()
    {
        int result = branch != null ? branch.hashCode() : 0;
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        return result;
    }
}
