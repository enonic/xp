package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.task.ProgressReporter;

final class UnpublishContentProgressListener
    extends BasePushContentProgressListener
{

    public UnpublishContentProgressListener( final ProgressReporter progressReporter )
    {
        super( progressReporter );
    }

    @Override
    public void contentResolved( final int count )
    {
        total = count;
        progressReporter.progress( progressCount, total );
    }
}
