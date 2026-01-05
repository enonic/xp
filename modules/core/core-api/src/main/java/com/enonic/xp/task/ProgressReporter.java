package com.enonic.xp.task;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ProgressReporter
{
    void progress( int current, int total );

    /**
     * Reports task progress with detailed control over current value, total items, and status message.
     *
     * @param current current progress value. If null, current value is unmodified
     * @param total total items to progress through. If null and no total was previously set, the total is unknown. If null and a total was previously set, the total remains unchanged. The total can be updated during progress
     * @param message a string shown in task status. If null, current status message is not modified
     */
    void progress( Integer current, Integer total, String message );

    void info( String message );
}
