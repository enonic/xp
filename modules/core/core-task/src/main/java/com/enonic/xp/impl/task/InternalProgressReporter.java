package com.enonic.xp.impl.task;

import com.enonic.xp.task.ProgressReporter;

public interface InternalProgressReporter
    extends ProgressReporter
{
    void running();

    void finished();

    void failed( String message );
}
