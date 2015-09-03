package com.enonic.wem.repo.internal.storage;

import com.enonic.xp.node.NodePath;

public class VersionPathCacheKey
    implements CacheKey
{
    private NodePath nodePath;

    public VersionPathCacheKey( final NodePath nodePath )
    {
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

        final VersionPathCacheKey that = (VersionPathCacheKey) o;

        return !( nodePath != null ? !nodePath.equals( that.nodePath ) : that.nodePath != null );

    }

    @Override
    public int hashCode()
    {
        return nodePath != null ? nodePath.hashCode() : 0;
    }
}
