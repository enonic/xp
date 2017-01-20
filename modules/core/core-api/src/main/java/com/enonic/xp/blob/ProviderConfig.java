package com.enonic.xp.blob;

import java.util.Map;

public interface ProviderConfig
{
    Map<Segment, String> segments();

    String readThroughProvider();

    boolean readThroughEnabled();

    long readThroughSizeThreshold();

    boolean isValid();
}
