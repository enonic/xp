package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.task.ProgressReporter;

public final class UnpublishContentProgressListener
    implements PushContentListener
{

    private final ProgressReporter progressReporter;

    private int total = 0;

    private int progressCount = 0;

    public UnpublishContentProgressListener( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void contentPushed( final int count )
    {
        progressCount = progressCount + count;
        progressReporter.progress( progressCount, total );
    }

    @Override
    public void contentResolved( final int count )
    {
        total = count;
        progressReporter.progress( progressCount, total );
    }
}
