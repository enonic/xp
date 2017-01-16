package com.enonic.xp.trace;

public interface TraceLocation
{
    String getMethod();

    String getClassName();

    int getLineNumber();
}
