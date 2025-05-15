package com.enonic.xp.repository;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;

public final class RepositorySegmentUtils
{
    private RepositorySegmentUtils()
    {
    }

    public static final int REPOSITORY_LEVEL = 0;

    public static final int BLOB_TYPE_LEVEL = 1;

    public static SegmentLevel toSegmentLevel( final RepositoryId repositoryId )
    {
        return SegmentLevel.from( repositoryId.toString() );
    }

    public static Segment toSegment( final RepositoryId repositoryId, final SegmentLevel blobType )
    {
        return Segment.create().
            level( toSegmentLevel( repositoryId ) ).
            level( blobType ).
            build();
    }

    public static RepositoryId toRepositoryId( final Segment segment )
    {
        final String repositoryId = segment.getLevel( REPOSITORY_LEVEL ).
            getValue();
        return RepositoryId.from( repositoryId );
    }

    public static boolean hasBlobTypeLevel( final Segment segment, final SegmentLevel blobType )
    {
        return segment.getLevel( BLOB_TYPE_LEVEL ).equals( blobType );
    }
}
