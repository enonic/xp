package com.enonic.xp.repo.impl.vacuum.snapshots;

import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger( SnapshotsVacuumTask.class );

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
        try
        {
            final DeleteSnapshotsResult deleteSnapshotsResult = snapshotService.delete(
                DeleteSnapshotParams.create().before( Instant.now().minusMillis( params.getAgeThreshold() ) ).build() );

            deleteSnapshotsResult.stream().forEach( snapshot -> builder.processed() );

            return builder.build();
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to vacuum snapshots", e );
            return builder.failed().build();
        }
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
