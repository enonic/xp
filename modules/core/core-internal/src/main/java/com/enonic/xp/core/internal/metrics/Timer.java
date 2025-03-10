package com.enonic.xp.core.internal.metrics;

public interface Timer
{
    void record(Runnable runnable);
}
