package com.enonic.wem.repo.internal.storage;

import com.enonic.xp.node.NodeVersionId;

public class VersionIdCacheKey
    implements CacheKey
{
    private NodeVersionId nodeVersionId;

    public VersionIdCacheKey( final NodeVersionId nodeVersionId )
    {
        this.nodeVersionId = nodeVersionId;
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

        final VersionIdCacheKey that = (VersionIdCacheKey) o;

        return !( nodeVersionId != null ? !nodeVersionId.equals( that.nodeVersionId ) : that.nodeVersionId != null );

    }

    @Override
    public int hashCode()
    {
        return nodeVersionId != null ? nodeVersionId.hashCode() : 0;
    }
}
