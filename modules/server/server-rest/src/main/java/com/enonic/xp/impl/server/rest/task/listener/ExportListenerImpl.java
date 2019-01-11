package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.export.NodeExportListener;
import com.enonic.xp.task.ProgressReporter;

public class ExportListenerImpl
    implements NodeExportListener
{
    private final ProgressReporter progressReporter;

    private int total = 0;

    private int current = 0;

    public ExportListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void nodeExported( final long count )
    {
        current += count;
        progressReporter.progress( current, total );
    }

    @Override
    public void nodeResolved( final long count )
    {
        total = Math.toIntExact( count );
    }
}
