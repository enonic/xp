package com.enonic.xp.trace;

@FunctionalInterface
public interface TraceRunnable<T>
{
    T run();
}
