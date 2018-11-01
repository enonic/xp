package com.enonic.xp.blob;

public interface ProviderConfig
{
    String readThroughProvider();

    boolean readThroughEnabled();

    long readThroughSizeThreshold();

    boolean isValid();
}
