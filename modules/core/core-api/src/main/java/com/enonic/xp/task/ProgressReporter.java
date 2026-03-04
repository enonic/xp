package com.enonic.xp.task;

public interface ProgressReporter
{
    /**
     *
     * Updates the task progress.
     *
     * @param params {ProgressReportParams} params - The progress report parameters.
     */
    void progress( ProgressReportParams params );

    /**
     * @deprecated Use {@link #progress(ProgressReportParams)} instead
     */

    @Deprecated
    void progress( int current, int total );

    /**
     * @deprecated Use {@link #progress(ProgressReportParams)} instead
     */
    @Deprecated
    void info( String message );
}
