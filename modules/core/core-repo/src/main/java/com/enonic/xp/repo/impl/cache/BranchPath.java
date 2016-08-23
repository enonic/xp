package com.enonic.xp.repo.impl.cache;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.node.NodePath;

public class BranchPath
    implements CachePath
{
    private final BranchId branchId;

    private final NodePath path;

    public BranchPath( final BranchId branchId, final NodePath path )
    {
        this.branchId = branchId;
        this.path = path;
    }

    @Override
    public CachePath getParentPath()
    {
        return new BranchPath( this.branchId, path.getParentPath() );
    }

    @Override
    public String toString()
    {
        return branchId != null ? branchId.getValue() + ":" + path : null;
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

        if ( branchId != null ? !branchId.equals( that.branchId ) : that.branchId != null )
        {
            return false;
        }
        return !( path != null ? !path.equals( that.path ) : that.path != null );
    }

    @Override
    public int hashCode()
    {
        int result = branchId != null ? branchId.hashCode() : 0;
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        return result;
    }
}
