package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.export.NodeImportListener;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;

public class ImportListenerImpl
    implements NodeImportListener
{
    private final ProgressReporter progressReporter;

    private int total;

    private long current;

    public ImportListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void nodeImported( final long count )
    {
        current = Math.addExact( current, count );
        progressReporter.progress( ProgressReportParams.create( Math.toIntExact( current ), total ).build() );
    }

    @Override
    public void nodeResolved( final long count )
    {
        total = Math.toIntExact( count );
    }
}
