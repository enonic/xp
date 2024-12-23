package com.enonic.xp.repo.impl.vacuum.blob;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.vacuum.VacuumListener;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.jupiter.api.Assertions.assertEquals;


public abstract class AbstractBlobVacuumTaskTest
{
    protected BlobStore blobStore;

    protected NodeService nodeService;

    protected Segment segment;

    public void setUp()
        throws Exception
    {
        this.blobStore = new MemoryBlobStore();
        this.nodeService = Mockito.mock( NodeService.class );
        Mockito.when( nodeService.findVersions( Mockito.any( NodeVersionQuery.class ) ) ).
            thenAnswer( ( invocation ) -> {
                final NodeVersionQuery query = invocation.getArgument( 0 );
                final ValueFilter valueFilter = (ValueFilter) query.getQueryFilters().first();
                if ( valueFilter.getValues().contains( ValueFactory.newString( BlobKey.from( ByteSource.wrap( "a-stuff".getBytes() ) ).toString() ) ) )
                {
                    return NodeVersionQueryResult.empty( 1 );
                }
                return NodeVersionQueryResult.empty( 0 );
            } );
    }

    public void test_delete_unused()
        throws Exception
    {
        this.blobStore.addRecord( segment, ByteSource.wrap( "a-stuff".getBytes() ) );
        this.blobStore.addRecord( segment, ByteSource.wrap( "b-stuff".getBytes() ) );
        this.blobStore.addRecord( segment, ByteSource.wrap( "c-stuff".getBytes() ) );

        final VacuumTask task = createTask();

        final VacuumTaskResult result = task.execute( VacuumTaskParams.create().vacuumStartedAt( Instant.now() ).ageThreshold( 0 ).build() );

        assertEquals( 3, result.getProcessed() );
        assertEquals( 2, result.getDeleted() );
        assertEquals( 1, result.getInUse() );
    }

    public void test_progress_report()
        throws Exception
    {
        this.blobStore.addRecord( segment, ByteSource.wrap( "a-stuff".getBytes() ) );
        this.blobStore.addRecord( segment, ByteSource.wrap( "b-stuff".getBytes() ) );
        this.blobStore.addRecord( segment, ByteSource.wrap( "c-stuff".getBytes() ) );

        final VacuumTask task = createTask();

        AtomicInteger blobReportCount = new AtomicInteger( 0 );
        final VacuumListener progressListener = new VacuumListener()
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
                blobReportCount.incrementAndGet();
            }
        };
        final VacuumTaskResult result = task.execute(
            VacuumTaskParams.create().vacuumStartedAt( Instant.now() ).ageThreshold( 0 ).listener( progressListener ).build() );

        assertEquals( 3, result.getProcessed() );
        assertEquals( 2, result.getDeleted() );
        assertEquals( 1, result.getInUse() );

        assertEquals( 3, blobReportCount.get() );
    }

    public void age_threshold()
        throws Exception
    {
        this.blobStore.addRecord( segment, ByteSource.wrap( "a-stuff".getBytes() ) );

        final VacuumTask task = createTask();
        final VacuumTaskResult result = task.execute( VacuumTaskParams.create().vacuumStartedAt( Instant.now() ).build() );

        assertEquals( 0, result.getProcessed() );
    }

    protected abstract VacuumTask createTask();
}
