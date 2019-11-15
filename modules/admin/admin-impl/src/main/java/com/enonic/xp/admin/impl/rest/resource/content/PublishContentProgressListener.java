package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.task.ProgressReporter;

public final class PublishContentProgressListener
    implements PushContentListener
{

    private final ProgressReporter progressReporter;

    private int total = 0;

    private int progressCount = 0;

    public PublishContentProgressListener( final ProgressReporter progressReporter )
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
        total = count * 2; // progress for resolving + copying
        progressReporter.progress( progressCount, total );
    }
}
