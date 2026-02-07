package com.enonic.xp.trace;

import java.io.IOException;

@FunctionalInterface
public interface TraceIO<T>
{
    T call()
        throws IOException;
}
