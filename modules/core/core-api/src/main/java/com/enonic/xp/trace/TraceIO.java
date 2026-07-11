package com.enonic.xp.trace;

import java.io.IOException;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@FunctionalInterface
public interface TraceIO<T extends @Nullable Object>
{
    T call()
        throws IOException;
}
