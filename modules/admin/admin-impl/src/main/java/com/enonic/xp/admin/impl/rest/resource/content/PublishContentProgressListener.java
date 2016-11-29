package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.task.ProgressReporter;

final class PublishContentProgressListener
    extends BasePushContentProgressListener
{

    public PublishContentProgressListener( final ProgressReporter progressReporter )
    {
        super( progressReporter );
    }

    @Override
    public void contentResolved( final int count )
    {
        total = count * 2; // progress for resolving + copying
        progressReporter.progress( progressCount, total );
    }
}
