package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.export.NodeExportListener;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;

public class ExportListenerImpl
    implements NodeExportListener
{
    private final ProgressReporter progressReporter;

    private int total;

    private int current;

    public ExportListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void nodeExported( final int count )
    {
        current += count;
        progressReporter.progress( ProgressReportParams.create( Math.toIntExact( current ), total ).build() );
    }

    @Override
    public void nodeResolved( final int count )
    {
        total = count;
    }
}
