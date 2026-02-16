package com.enonic.xp.repo.impl.vacuum;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VacuumServiceImplTest
{

    @Test
    public void runTasks()
        throws Exception
    {
        final SnapshotService snapshotService = mock( SnapshotService.class );
        when( snapshotService.delete( mock( DeleteSnapshotParams.class ) ) ).thenReturn( DeleteSnapshotsResult.create().build() );

        final VacuumServiceImpl service = new VacuumServiceImpl( snapshotService );
        service.activate( mock( VacuumConfig.class, i -> i.getMethod().getDefaultValue() ) );

        service.addTask( new VacuumTask()
        {
            @Override
            public VacuumTaskResult execute( final VacuumTaskParams params )
            {
                return VacuumTaskResult.create().processed().build();
            }

            @Override
            public int order()
            {
                return 10;
            }

            @Override
            public String name()
            {
                return "ATask";
            }

            @Override
            public boolean deletesBlobs()
            {
                return false;
            }

        } );

        service.addTask( new VacuumTask()
        {
            @Override
            public VacuumTaskResult execute( final VacuumTaskParams params )
            {
                return VacuumTaskResult.create().failed().build();
            }

            @Override
            public int order()
            {
                return 0;
            }

            @Override
            public String name()
            {
                return "AnotherTask";
            }

            @Override
            public boolean deletesBlobs()
            {
                return false;
            }
        } );

        final VacuumResult result = NodeHelper.runAsAdmin( () -> service.vacuum( VacuumParameters.create().build() ) );

        assertEquals( 2, result.getResults().size() );

        verify( snapshotService, times( 0 ) ).delete( any( DeleteSnapshotParams.class ) );
    }

    @Test
    public void runTasksWithDeletingSnapshots()
    {
        final SnapshotService snapshotService = mock( SnapshotService.class );
        when( snapshotService.delete( any( DeleteSnapshotParams.class ) ) ).thenReturn( DeleteSnapshotsResult.create().build() );

        final VacuumServiceImpl service = new VacuumServiceImpl( snapshotService );
        service.activate( mock( VacuumConfig.class, i -> i.getMethod().getDefaultValue() ) );

        final NodeService nodeService = mock( NodeService.class );
        final BlobStore blobStore = mock( BlobStore.class );

        service.addTask( new BinaryBlobVacuumTask( nodeService, blobStore ) );
        service.addTask( new NodeBlobVacuumTask( nodeService, blobStore ) );

        final VacuumResult result = NodeHelper.runAsAdmin( () -> service.vacuum( VacuumParameters.create().build() ) );

        assertEquals( 2, result.getResults().size() );

        verify( snapshotService, times( 1 ) ).delete( any( DeleteSnapshotParams.class ) );
    }

    @Test
    public void runTasksWithDeletingSnapshotsFailed()
    {
        final SnapshotService snapshotService = mock( SnapshotService.class );

        when( snapshotService.delete( any( DeleteSnapshotParams.class ) ) ).thenThrow( RuntimeException.class );

        final VacuumServiceImpl service = new VacuumServiceImpl( snapshotService );
        service.activate( mock( VacuumConfig.class, i -> i.getMethod().getDefaultValue() ) );

        final NodeService nodeService = mock( NodeService.class );
        final BlobStore blobStore = mock( BlobStore.class );

        service.addTask( new BinaryBlobVacuumTask( nodeService, blobStore ) );
        service.addTask( new NodeBlobVacuumTask( nodeService, blobStore ) );

        final VacuumResult result = NodeHelper.runAsAdmin( () -> service.vacuum( VacuumParameters.create().build() ) );

        assertEquals( 2, result.getResults().size() );

        verify( snapshotService, times( 1 ) ).delete( any( DeleteSnapshotParams.class ) );
    }
}
