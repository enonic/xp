package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.upgrade.UpgradeListener;

public class UpgradeListenerImpl
    implements UpgradeListener
{
    private final ProgressReporter progressReporter;

    private int total = 0;

    private int current = 0;

    public UpgradeListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void total( final long total )
    {
        this.total = Math.toIntExact( total );
    }

    @Override
    public void upgraded()
    {
        this.progressReporter.progress( ++current, total );
    }

    @Override
    public void finished()
    {
        if ( total == 0 )
        {
            this.progressReporter.progress( 1, 1 );
            return;
        }

        if ( current < total )
        {
            this.progressReporter.progress( total, total );
        }
    }
}
