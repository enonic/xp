package com.enonic.xp.blob;

import java.util.Map;

public interface ProviderConfig
{
    public static final Segment[] DEFAULT_REQUIRED_SEGMENTS = new Segment[]{Segment.from( "node" ), Segment.from( "binary" )};

    static final String READ_THROUGH_PROVIDER = "readThrough.provider";

    static final String READ_THROUGH_ENABLED = "readThrough.enabled";

    static final String READ_THROUGH_SIZE_THRESHOLD = "readThrough.sizeThreshold";

    Map<Segment, String> segments();

    String readThroughProvider();

    boolean readThroughEnabled();

    long readThroughSizeThreshold();

    boolean isValid();

}
