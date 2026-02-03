package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.export.NodeImportListener;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;

public class ImportListenerImpl
    implements NodeImportListener
{
    private final ProgressReporter progressReporter;

    private int total;

    private int current;

    public ImportListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void nodeImported( final int count )
    {
        addProgress( count );
    }

    @Override
    public void nodeSkipped( final int count )
    {
        addProgress( count );
    }

    @Override
    public void nodeResolved( final int count )
    {
        total = count;
    }

    private void addProgress( final int count )
    {
        current += count;
        progressReporter.progress( ProgressReportParams.create( current, total ).build() );
    }
}
