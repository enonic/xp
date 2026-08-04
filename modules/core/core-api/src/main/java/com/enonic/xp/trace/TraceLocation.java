package com.enonic.xp.trace;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TraceLocation
{
    String getMethod();

    String getClassName();

    int getLineNumber();
}
