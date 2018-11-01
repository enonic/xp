package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.task.ProgressReporter;

public final class ApplyPermissionsProgressListener
    implements ApplyPermissionsListener
{
    private final ProgressReporter progressReporter;

    private int total = 0;

    private float progressCount = 0;

    public ApplyPermissionsProgressListener( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void setTotal( final int count )
    {
        total = count;
    }

    @Override
    public void permissionsApplied( final int count )
    {
        progressCount += count;
        progressReporter.progress( Math.round( progressCount ), total );
    }

    @Override
    public void notEnoughRights( final int count )
    {
        progressCount += count;
        progressReporter.progress( Math.round( progressCount ), total );
    }
}
