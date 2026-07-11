package com.enonic.xp.trace;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@FunctionalInterface
public interface TraceRunnable<T extends @Nullable Object>
{
    T run();
}
