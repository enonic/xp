package com.enonic.xp.repo.impl.vacuum.binary;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.internal.blobstore.MemoryBlobRecord;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.vacuum.VacuumListener;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.Assert.*;


public class UnusedBinaryFileCleanerTaskTest
{

    private BlobStore blobStore;

    @Before
    public void setUp()
        throws Exception
    {
        this.blobStore = new MemoryBlobStore();
    }

    @Test
    public void test_delete_unused()
        throws Exception
    {
        this.blobStore.addRecord( NodeConstants.BINARY_SEGMENT, createBinaryRecord( 'a' ) );
        this.blobStore.addRecord( NodeConstants.BINARY_SEGMENT, createBinaryRecord( 'b' ) );
        this.blobStore.addRecord( NodeConstants.BINARY_SEGMENT, createBinaryRecord( 'c' ) );

        this.blobStore.addRecord( NodeConstants.NODE_SEGMENT, createVersionRecordWithBinaryRef( "1", 'a' ) );

        final UnusedBinaryFileCleanerTask task = new UnusedBinaryFileCleanerTask();
        task.setBlobStore( this.blobStore );

        final VacuumTaskResult result = task.execute( VacuumTaskParams.create().ageThreshold( 0 ).build() );

        assertEquals( 3, result.getProcessed() );
        assertEquals( 2, result.getDeleted() );
        assertEquals( 1, result.getInUse() );
    }

    @Test
    public void test_progress_report()
        throws Exception
    {
        this.blobStore.addRecord( NodeConstants.BINARY_SEGMENT, createBinaryRecord( 'a' ) );
        this.blobStore.addRecord( NodeConstants.BINARY_SEGMENT, createBinaryRecord( 'b' ) );
        this.blobStore.addRecord( NodeConstants.BINARY_SEGMENT, createBinaryRecord( 'c' ) );

        this.blobStore.addRecord( NodeConstants.NODE_SEGMENT, createVersionRecordWithBinaryRef( "1", 'a' ) );

        final UnusedBinaryFileCleanerTask task = new UnusedBinaryFileCleanerTask();
        task.setBlobStore( this.blobStore );

        AtomicInteger blobReportCount = new AtomicInteger( 0 );
        final VacuumListener progressListener = new VacuumListener()
        {
            @Override
            public void vacuumingBlobSegment( final Segment segment )
            {
                assertEquals( Segment.from( "binary" ), segment );
            }

            @Override
            public void vacuumingBlob( final long count )
            {
                blobReportCount.incrementAndGet();
            }

            @Override
            public void vacuumingVersionRepository( final RepositoryId repository, final long total )
            {

            }

            @Override
            public void vacuumingVersion( final long count )
            {

            }
        };
        final VacuumTaskResult result = task.execute( VacuumTaskParams.create().ageThreshold( 0 ).listener( progressListener ).build() );

        assertEquals( 3, result.getProcessed() );
        assertEquals( 2, result.getDeleted() );
        assertEquals( 1, result.getInUse() );

        assertEquals( 3, blobReportCount.get() );
    }

    @Test
    public void age_threshold()
        throws Exception
    {
        this.blobStore.addRecord( NodeConstants.BINARY_SEGMENT, createBinaryRecord( 'a' ) );

        final UnusedBinaryFileCleanerTask task = new UnusedBinaryFileCleanerTask();
        task.setBlobStore( this.blobStore );

        final VacuumTaskResult result = task.execute( VacuumTaskParams.create().build() );

        assertEquals( 0, result.getProcessed() );
    }

    private MemoryBlobRecord createVersionRecordWithBinaryRef( final String key, final char binaryRef )
    {
        return new MemoryBlobRecord( BlobKey.from( key ),
                                     ByteSource.wrap( createVersionContent( createBlobKey( binaryRef ) ).getBytes() ) );
    }

    private MemoryBlobRecord createBinaryRecord( final char id )
    {
        return new MemoryBlobRecord( createBlobKey( id ), ByteSource.wrap( "stuff".getBytes() ) );
    }

    private BlobKey createBlobKey( final char value )
    {
        return BlobKey.from( Strings.padStart( "", 40, value ) );
    }

    private String createVersionContent( final BlobKey blobKey )
    {
        return "{\"attachedBinaries\":[{\"binaryReference\":\"trump3.jpg\",\"blobKey\":\"" + blobKey +
            "\"}],\"childOrder\":\"modifiedtime DESC\",\"data\"";
    }
}