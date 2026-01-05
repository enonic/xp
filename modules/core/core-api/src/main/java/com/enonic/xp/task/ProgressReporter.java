package com.enonic.xp.task;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ProgressReporter
{
    /**
     * @deprecated Use {@link #progress(ProgressReportParams)} instead
     */
    @Deprecated
    void progress( int current, int total );

    /**
     * Updates the progress of the task.
     *
     * @param current current progress value. If null, current value is unmodified
     * @param total   total items to be processed. Initially if null, total is unknown. If total was set before, null means
     *                unchanged. Total can change during progress
     * @param message a string shown in task status. If null, current status message is not modified
     * @deprecated Use {@link #progress(ProgressReportParams)} instead
     */
    @Deprecated
    void progress( Integer current, Integer total, String message );

    /**
     * Updates the progress of the task.
     *
     * @param params the progress report parameters
     */
    void progress( ProgressReportParams params );

    void info( String message );
}
