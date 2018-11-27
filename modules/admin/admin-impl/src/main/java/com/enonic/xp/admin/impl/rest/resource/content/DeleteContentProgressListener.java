package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.DeleteContentListener;
import com.enonic.xp.task.ProgressReporter;

public final class DeleteContentProgressListener
    implements DeleteContentListener
{
    private final ProgressReporter progressReporter;

    private int total = 0;

    private int progressCount = 0;

    public DeleteContentProgressListener( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void setTotal( final int count )
    {
        total = count;
    }

    @Override
    public void contentDeleted( final int count )
    {
        progressCount = progressCount + count;
        progressReporter.progress( progressCount, total );
    }
}
