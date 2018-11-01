package com.enonic.xp.repository;

import com.enonic.xp.blob.SegmentLevel;

public class RepositorySegmentLevel
    extends SegmentLevel
{
    protected RepositorySegmentLevel( final RepositoryId repositoryId )
    {
        super( repositoryId.toString() );
    }

    public static RepositorySegmentLevel from( final RepositoryId repositoryId )
    {
        return new RepositorySegmentLevel( repositoryId );
    }
}
