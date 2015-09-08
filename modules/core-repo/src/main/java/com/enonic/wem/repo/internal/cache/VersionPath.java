package com.enonic.wem.repo.internal.cache;

import com.enonic.xp.node.NodePath;

public class VersionPath
    implements CachePath
{
    private NodePath nodePath;

    public VersionPath( final NodePath nodePath )
    {
        this.nodePath = nodePath;
    }

    @Override
    public CachePath getParentPath()
    {
        return new VersionPath( nodePath.getParentPath() );
    }

    @Override
    public String toString()
    {
        return nodePath.toString();
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

        final VersionPath that = (VersionPath) o;

        return !( nodePath != null ? !nodePath.equals( that.nodePath ) : that.nodePath != null );

    }

    @Override
    public int hashCode()
    {
        return nodePath != null ? nodePath.hashCode() : 0;
    }
}
