package com.enonic.xp.repo.impl.vacuum.snapshots;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.vacuum.VacuumListener;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SnapshotsVacuumTaskTest
{
    @Test
    void test()
    {
        SnapshotService snapshotService = mock( SnapshotService.class );
        when( snapshotService.delete( any( DeleteSnapshotParams.class ) ) ).thenReturn(
            DeleteSnapshotsResult.create().add( "snapshot" ).build() );

        SnapshotsVacuumTask instance = new SnapshotsVacuumTask( snapshotService );

        assertEquals( "SnapshotsVacuumTask", instance.name() );
        assertEquals( 400, instance.order() );

        VacuumTaskResult result = instance.execute( VacuumTaskParams.create().listener( new VacuumListener()
        {
            @Override
            public void vacuumBegin( final long taskCount )
            {

            }

            @Override
            public void taskBegin( final String task, final Long stepCount )
            {

            }

            @Override
            public void stepBegin( final String stepName, final Long toProcessCount )
            {

            }

            @Override
            public void processed( final long count )
            {
            }
        } ).ageThreshold( 60 * 1000 ).vacuumStartedAt( Instant.now() ).build() );

        assertEquals( "SnapshotsVacuumTask", result.getTaskName() );
        assertEquals( 1, result.getProcessed() );
        assertEquals( 0, result.getInUse() );
        assertEquals( 0, result.getDeleted() );
        assertEquals( 0, result.getFailed() );

        verify( snapshotService, times( 1 ) ).delete( any( DeleteSnapshotParams.class ) );
    }
}
