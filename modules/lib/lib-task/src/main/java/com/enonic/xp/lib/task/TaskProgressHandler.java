package com.enonic.xp.lib.task;

import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.TaskProgressReporterContext;

public final class TaskProgressHandler
{
    private Double current;

    private Double total;

    private String info;

    public void setCurrent( final Double current )
    {
        this.current = current;
    }

    public void setTotal( final Double total )
    {
        this.total = total;
    }

    public void setInfo( final String info )
    {
        this.info = info;
    }


    public void reportProgress()
    {
        final ProgressReporter progressReporter = TaskProgressReporterContext.current();
        if ( progressReporter == null )
        {
            throw new RuntimeException( "The reportProgress function must be called from within a task." );
        }
        final ProgressReportParams params = ProgressReportParams.create()
            .current( current != null ? current.intValue() : null )
            .total( total != null ? total.intValue() : null )
            .info( info )
            .build();
        progressReporter.progress( params );
    }

}
