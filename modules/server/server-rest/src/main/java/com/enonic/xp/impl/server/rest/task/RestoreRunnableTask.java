package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.impl.server.rest.model.RestoreResultJson;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public class RestoreRunnableTask
    implements RunnableTask
{
    private final RestoreParams restoreParams;

    private final SnapshotService snapshotService;

    private RestoreRunnableTask( Builder builder )
    {
        this.restoreParams = builder.restoreParams;
        this.snapshotService = builder.snapshotService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final String snapshotName = restoreParams.isLatest() ? "latest snapshot" : restoreParams.getSnapshotName();
        progressReporter.info( "Restoring from snapshot: " + snapshotName );

        final RestoreResult result = this.snapshotService.restore( restoreParams );

        if ( result.isFailed() )
        {
            progressReporter.info( "Restore failed: " + result.getMessage() );
        }
        else
        {
            progressReporter.info( "Restore completed successfully" );
        }
        progressReporter.progress( ProgressReportParams.create( RestoreResultJson.from( result ).toString() ).build() );
    }

    public static class Builder
    {
        private RestoreParams restoreParams;

        private SnapshotService snapshotService;

        public Builder restoreParams( final RestoreParams restoreParams )
        {
            this.restoreParams = restoreParams;
            return this;
        }

        public Builder snapshotService( final SnapshotService snapshotService )
        {
            this.snapshotService = snapshotService;
            return this;
        }

        public RestoreRunnableTask build()
        {
            return new RestoreRunnableTask( this );
        }
    }
}
