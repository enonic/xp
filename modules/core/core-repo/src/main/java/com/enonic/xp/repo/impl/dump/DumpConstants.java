package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.util.Version;
import com.enonic.xp.repo.impl.node.NodeConstants;

public class DumpConstants
{
    static final String META_BASE_PATH = "meta";

    public static final Segment DUMP_SEGMENT_NODES = NodeConstants.NODE_SEGMENT;

    public static final Segment DUMP_SEGMENT_BINARIES = NodeConstants.BINARY_SEGMENT;

    public static final Version MODEL_VERSION = Version.valueOf( "1.0.0" );

}
