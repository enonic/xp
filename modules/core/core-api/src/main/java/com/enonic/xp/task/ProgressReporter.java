package com.enonic.xp.task;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ProgressReporter
{
    void progress( int current, int total );

    void progress( int current, int total, String message );

    void info( String message );
}
