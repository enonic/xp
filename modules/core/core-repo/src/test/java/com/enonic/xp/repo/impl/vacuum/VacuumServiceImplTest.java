package com.enonic.xp.repo.impl.vacuum;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.vacuum.blob.BinaryBlobVacuumTask;
import com.enonic.xp.repo.impl.vacuum.blob.NodeBlobVacuumTask;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VacuumServiceImplTest
{
    @Test
    void runTasks()
    {
        final SnapshotService snapshotService = mock( SnapshotService.class );
        when( snapshotService.delete( mock( DeleteSnapshotParams.class ) ) ).thenReturn( DeleteSnapshotsResult.create().build() );

        final VacuumServiceImpl service = new VacuumServiceImpl( snapshotService );
        service.activate( mock( VacuumConfig.class, i -> i.getMethod().getDefaultValue() ) );

        service.addTask( createTask( "ATask", 10, false ) );
        service.addTask( createTask( "AnotherTask", 0, false ) );

        final VacuumResult result = NodeHelper.runAsAdmin(
            () -> service.vacuum( VacuumParameters.create().taskNames( Set.of( "ATask", "AnotherTask" ) ).build() ) );

        assertEquals( 2, result.getResults().size() );

        verify( snapshotService, never() ).delete( any( DeleteSnapshotParams.class ) );
    }

    @Test
    void runTasksWithDeletingSnapshots()
    {
        final SnapshotService snapshotService = mock( SnapshotService.class );
        when( snapshotService.delete( any( DeleteSnapshotParams.class ) ) ).thenReturn( DeleteSnapshotsResult.create().build() );

        final VacuumServiceImpl service = new VacuumServiceImpl( snapshotService );
        service.activate( mock( VacuumConfig.class, i -> i.getMethod().getDefaultValue() ) );

        final NodeService nodeService = mock( NodeService.class );
        final BlobStore blobStore = mock( BlobStore.class );

        service.addTask( new BinaryBlobVacuumTask( nodeService, blobStore ) );
        service.addTask( new NodeBlobVacuumTask( nodeService, blobStore ) );

        final VacuumResult result = NodeHelper.runAsAdmin(
            () -> service.vacuum( VacuumParameters.create().taskNames( Set.of( "BinaryBlobVacuumTask", "NodeBlobVacuumTask" ) ).build() ) );

        assertEquals( 2, result.getResults().size() );

        verify( snapshotService, times( 1 ) ).delete( any( DeleteSnapshotParams.class ) );
    }

    @Test
    void defaultTaskNames_onlyVersionTableVacuumTask()
    {
        final SnapshotService snapshotService = mock( SnapshotService.class );

        final VacuumServiceImpl service = new VacuumServiceImpl( snapshotService );
        service.activate( mock( VacuumConfig.class, i -> i.getMethod().getDefaultValue() ) );

        service.addTask( createTask( "VersionTableVacuumTask", 0, false ) );
        service.addTask( createTask( "BinaryBlobVacuumTask", 10, true ) );

        final VacuumResult result = NodeHelper.runAsAdmin( () -> service.vacuum( VacuumParameters.create().build() ) );

        assertEquals( 1, result.getResults().size() );

        verify( snapshotService, never() ).delete( any( DeleteSnapshotParams.class ) );
    }

    @Test
    void runTasksWithDeletingSnapshotsFailed()
    {
        final SnapshotService snapshotService = mock( SnapshotService.class );

        when( snapshotService.delete( any( DeleteSnapshotParams.class ) ) ).thenThrow( RuntimeException.class );

        final VacuumServiceImpl service = new VacuumServiceImpl( snapshotService );
        service.activate( mock( VacuumConfig.class, i -> i.getMethod().getDefaultValue() ) );

        final NodeService nodeService = mock( NodeService.class );
        final BlobStore blobStore = mock( BlobStore.class );

        service.addTask( new BinaryBlobVacuumTask( nodeService, blobStore ) );
        service.addTask( new NodeBlobVacuumTask( nodeService, blobStore ) );

        final VacuumResult result = NodeHelper.runAsAdmin(
            () -> service.vacuum( VacuumParameters.create().taskNames( Set.of( "BinaryBlobVacuumTask", "NodeBlobVacuumTask" ) ).build() ) );

        assertEquals( 2, result.getResults().size() );

        verify( snapshotService, times( 1 ) ).delete( any( DeleteSnapshotParams.class ) );
    }

    private static VacuumTask createTask( final String name, final int order, final boolean deletesBlobs )
    {
        return new VacuumTask()
        {
            @Override
            public VacuumTaskResult execute( final VacuumTaskParams params )
            {
                return VacuumTaskResult.create().processed().build();
            }

            @Override
            public int order()
            {
                return order;
            }

            @Override
            public String name()
            {
                return name;
            }

            @Override
            public boolean deletesBlobs()
            {
                return deletesBlobs;
            }
        };
    }
}
