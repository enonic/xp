package com.enonic.xp.task;

import com.google.common.annotations.Beta;

@Beta
public interface ProgressReporter
{
    void progress( int current, int total);

    void info( String message );
}
