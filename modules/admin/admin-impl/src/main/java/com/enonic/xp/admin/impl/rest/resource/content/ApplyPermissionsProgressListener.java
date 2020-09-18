package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.task.ProgressReporter;

public final class ApplyPermissionsProgressListener
    implements ApplyPermissionsListener
{
    private final ProgressReporter progressReporter;

    private int total;

    private long current;

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
        current = Math.addExact( current, count );
        progressReporter.progress( Math.toIntExact( current ), total );
    }

    @Override
    public void notEnoughRights( final int count )
    {
        current = Math.addExact( current, count );
        progressReporter.progress( Math.toIntExact( current ), total );
    }
}
