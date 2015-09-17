package com.enonic.wem.repo.internal.storage;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;

public class BranchPathCacheKey
    implements CacheKey
{
    private Branch branch;

    private NodePath nodePath;

    public BranchPathCacheKey( final Branch branch, final NodePath nodePath )
    {
        this.branch = branch;
        this.nodePath = nodePath;
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

        final BranchPathCacheKey that = (BranchPathCacheKey) o;

        if ( branch != null ? !branch.equals( that.branch ) : that.branch != null )
        {
            return false;
        }
        return !( nodePath != null ? !nodePath.equals( that.nodePath ) : that.nodePath != null );

    }

    @Override
    public int hashCode()
    {
        int result = branch != null ? branch.hashCode() : 0;
        result = 31 * result + ( nodePath != null ? nodePath.hashCode() : 0 );
        return result;
    }
}
