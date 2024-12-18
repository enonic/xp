package com.enonic.xp.repo.impl.vacuum.snapshots;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class SnapshotsVacuumTask
    implements VacuumTask
{
    private static final int ORDER = 400;

    private static final String NAME = "SnapshotsVacuumTask";

    private final SnapshotService snapshotService;

    @Activate
    public SnapshotsVacuumTask( @Reference final SnapshotService snapshotService )
    {
        this.snapshotService = snapshotService;
    }

    @Override
    public VacuumTaskResult execute( final VacuumTaskParams params )
    {
        if ( params.hasListener() )
        {
            params.getListener().taskBegin( NAME, null );
        }

        final VacuumTaskResult.Builder builder = VacuumTaskResult.create().taskName( NAME );
        final DeleteSnapshotsResult deleteSnapshotsResult =
            snapshotService.delete( DeleteSnapshotParams.create().before( params.getVacuumStartedAt().minusMillis( params.getAgeThreshold() ) ).build() );

        deleteSnapshotsResult.getDeletedSnapshots().forEach( snapshot -> builder.processed() );
        deleteSnapshotsResult.getFailedSnapshots().forEach( snapshot -> builder.failed() );

        return builder.build();
    }

    @Override
    public int order()
    {
        return ORDER;
    }

    @Override
    public String name()
    {
        return NAME;
    }
}
