package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.task.ProgressReporter;

abstract class BasePushContentProgressListener
    implements PushContentListener
{
    protected final ProgressReporter progressReporter;

    protected int total = 0;

    protected int progressCount = 0;

    public BasePushContentProgressListener( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void contentPushed( final int count )
    {
        progressCount = progressCount + count;
        progressReporter.progress( progressCount, total );
    }
}
