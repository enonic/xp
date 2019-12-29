package com.enonic.xp.task;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ProgressReporter
{
    void progress( int current, int total );

    void info( String message );
}
