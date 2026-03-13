package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.impl.server.rest.model.SnapshotResultJson;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public class SnapshotRunnableTask
    implements RunnableTask
{
    private final SnapshotParams snapshotParams;

    private final SnapshotService snapshotService;

    private SnapshotRunnableTask( Builder builder )
    {
        this.snapshotParams = builder.snapshotParams;
        this.snapshotService = builder.snapshotService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        progressReporter.info( "Creating snapshot: " + snapshotParams.getSnapshotName() );

        final SnapshotResult result = this.snapshotService.snapshot( snapshotParams );

        progressReporter.info( "Snapshot completed with state: " + result.getState() );
        progressReporter.progress( ProgressReportParams.create( SnapshotResultJson.from( result ).toString() ).build() );
    }

    public static class Builder
    {
        private SnapshotParams snapshotParams;

        private SnapshotService snapshotService;

        public Builder snapshotParams( final SnapshotParams snapshotParams )
        {
            this.snapshotParams = snapshotParams;
            return this;
        }

        public Builder snapshotService( final SnapshotService snapshotService )
        {
            this.snapshotService = snapshotService;
            return this;
        }

        public SnapshotRunnableTask build()
        {
            return new SnapshotRunnableTask( this );
        }
    }
}
