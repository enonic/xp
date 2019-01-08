package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.export.NodeImportListener;
import com.enonic.xp.task.ProgressReporter;

public class ImportListenerImpl
    implements NodeImportListener
{
    private final ProgressReporter progressReporter;

    private int total = 0;

    private int current = 0;

    public ImportListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void nodeImported( final long count )
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
