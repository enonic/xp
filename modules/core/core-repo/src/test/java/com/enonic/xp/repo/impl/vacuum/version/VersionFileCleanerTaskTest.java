package com.enonic.xp.repo.impl.vacuum.version;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.Assert.*;

public class VersionFileCleanerTaskTest
{
    private NodeInUseDetector detector;

    private NodeIdResolver resolver;

    @Before
    public void setUp()
        throws Exception
    {
        this.detector = Mockito.mock( NodeInUseDetector.class );
        this.resolver = Mockito.mock( NodeIdResolver.class );
    }


    @Test
    public void age_threshold()
        throws Exception
    {
        final MemoryBlobStore blobStore = new MemoryBlobStore();
        final BlobRecord rec1 = blobStore.addRecord( NodeConstants.NODE_SEGMENT, ByteSource.wrap( "entry1".getBytes() ) );

        final VersionFileCleanerTask task = new VersionFileCleanerTask();
        task.setBlobStore( blobStore );
        task.setNodeInUseDetector( this.detector );
        task.setNodeIdResolver( this.resolver );

        final NodeId node1 = NodeId.from( "node1" );

        Mockito.when( this.resolver.resolve( rec1 ) ).thenReturn( node1 );

        final VacuumTaskResult result = task.execute( VacuumTaskParams.create().build() );
        assertEquals( 0, result.getProcessed() );

        assertNotNull( blobStore.getRecord( NodeConstants.NODE_SEGMENT, rec1.getKey() ) );
    }

    @Test
    public void delete_if_not_in_use()
        throws Exception
    {
        final MemoryBlobStore blobStore = new MemoryBlobStore();
        final BlobRecord rec1 = blobStore.addRecord( NodeConstants.NODE_SEGMENT, ByteSource.wrap( "entry1".getBytes() ) );
        final BlobRecord rec2 = blobStore.addRecord( NodeConstants.NODE_SEGMENT, ByteSource.wrap( "entry2".getBytes() ) );

        final VersionFileCleanerTask task = new VersionFileCleanerTask();
        task.setBlobStore( blobStore );
        task.setNodeInUseDetector( this.detector );
        task.setNodeIdResolver( this.resolver );

        final NodeId node1 = NodeId.from( "node1" );
        final NodeId node2 = NodeId.from( "node2" );

        Mockito.when( this.resolver.resolve( rec1 ) ).thenReturn( node1 );
        Mockito.when( this.resolver.resolve( rec2 ) ).thenReturn( node2 );

        Mockito.when( this.detector.execute( node1 ) ).
            thenReturn( false );
        Mockito.when( this.detector.execute( node2 ) ).
            thenReturn( true );

        final VacuumTaskResult result = task.execute( VacuumTaskParams.create().ageThreshold( 0 ).build() );
        assertEquals( 2, result.getProcessed() );
        assertEquals( 1, result.getInUse() );
        assertEquals( 1, result.getDeleted() );

        assertNull( blobStore.getRecord( NodeConstants.NODE_SEGMENT, rec1.getKey() ) );
        assertNotNull( blobStore.getRecord( NodeConstants.NODE_SEGMENT, rec2.getKey() ) );
    }

    @Test
    public void do_not_delete_if_no_id_found()
        throws Exception
    {
        final MemoryBlobStore blobStore = new MemoryBlobStore();
        final BlobRecord rec1 = blobStore.addRecord( NodeConstants.NODE_SEGMENT, ByteSource.wrap( "entry1".getBytes() ) );

        final VersionFileCleanerTask task = new VersionFileCleanerTask();
        task.setBlobStore( blobStore );
        task.setNodeInUseDetector( this.detector );
        task.setNodeIdResolver( this.resolver );

        Mockito.when( this.resolver.resolve( rec1 ) ).thenReturn( null );

        final VacuumTaskResult result = task.execute( VacuumTaskParams.create().ageThreshold( 0 ).build() );
        assertEquals( 1, result.getProcessed() );
        assertEquals( 1, result.getFailed() );

        assertNotNull( blobStore.getRecord( NodeConstants.NODE_SEGMENT, rec1.getKey() ) );
    }

    @Test
    public void detector_throws_exception()
        throws Exception
    {
        final MemoryBlobStore blobStore = new MemoryBlobStore();
        final BlobRecord rec1 = blobStore.addRecord( NodeConstants.NODE_SEGMENT, ByteSource.wrap( "entry1".getBytes() ) );

        final VersionFileCleanerTask task = new VersionFileCleanerTask();
        task.setBlobStore( blobStore );
        task.setNodeInUseDetector( this.detector );
        task.setNodeIdResolver( this.resolver );

        final NodeId node1 = NodeId.from( "node1" );
        final NodeId node2 = NodeId.from( "node2" );

        Mockito.when( this.resolver.resolve( rec1 ) ).thenReturn( node1 );

        Mockito.when( this.detector.execute( node1 ) ).
            thenThrow( new RuntimeException( "help!" ) );

        final VacuumTaskResult result = task.execute( VacuumTaskParams.create().ageThreshold( 0 ).build() );
        assertEquals( 1, result.getProcessed() );
        assertEquals( 1, result.getFailed() );

        assertNotNull( blobStore.getRecord( NodeConstants.NODE_SEGMENT, rec1.getKey() ) );
    }
}