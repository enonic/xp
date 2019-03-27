package com.enonic.xp.repo.impl.node;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.repository.RepositorySegmentUtils;

public class NodeSegmentUtils
{
    public static boolean isNodeSegment( final Segment segment )
    {
        return RepositorySegmentUtils.hasBlobTypeLevel( segment, NodeConstants.NODE_SEGMENT_LEVEL );
    }

    public static boolean isBinarySegment( final Segment segment )
    {
        return RepositorySegmentUtils.hasBlobTypeLevel( segment, NodeConstants.BINARY_SEGMENT_LEVEL );
    }
}
