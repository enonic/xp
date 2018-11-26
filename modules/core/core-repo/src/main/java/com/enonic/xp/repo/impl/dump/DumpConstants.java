package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.util.Version;

public class DumpConstants
{
    static final String META_BASE_PATH = "meta";

    public static final SegmentLevel DUMP_NODE_SEGMENT_LEVEL = NodeConstants.NODE_SEGMENT_LEVEL;

    public static final SegmentLevel DUMP_BINARY_SEGMENT_LEVEL = NodeConstants.BINARY_SEGMENT_LEVEL;

    public static final Version MODEL_VERSION = new Version( 2, 0, 0 );
}
