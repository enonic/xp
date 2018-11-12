package com.enonic.xp.repo.impl.vacuum.segment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.vacuum.VacuumTaskResult;

public class SegmentCleanerTaskTest
{
    private MemoryBlobStore blobStore;

    private RepositoryId repositoryId;

    private RepositoryId repositoryId2;

    private Segment segment;

    private Segment segment2;

    private RepositoryService repositoryService;

    @Before
    public void setUp()
        throws Exception
    {
        this.blobStore = new MemoryBlobStore();
        this.repositoryId = RepositoryId.from( "test" );
        this.repositoryId2 = RepositoryId.from( "test2" );
        this.segment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL );
        this.segment2 = RepositorySegmentUtils.toSegment( repositoryId2, NodeConstants.NODE_SEGMENT_LEVEL );

        this.repositoryService = Mockito.mock( RepositoryService.class );

        final Branch branch = Branch.from( "master" );
        final Repository repository = Repository.create().id( repositoryId ).branches( branch ).build();
        final Repository repository2 = Repository.create().id( repositoryId ).branches( branch ).build();
        Mockito.when( repositoryService.list() ).thenReturn( Repositories.from( repository, repository2 ) );
    }

    @Test
    public void test()
    {
        final BlobRecord record = blobStore.addRecord( segment, ByteSource.wrap( "hello".getBytes() ) );
        final BlobRecord record2 = blobStore.addRecord( segment2, ByteSource.wrap( "hello".getBytes() ) );
        Assert.assertEquals( 2, blobStore.listSegments().count() );

        blobStore.removeRecord( segment, record2.getKey() );
        Assert.assertEquals( 2, blobStore.listSegments().count() );

        final SegmentCleanerTask task = new SegmentCleanerTask();
        task.setBlobStore( blobStore );
        task.setRepositoryService( repositoryService );

        final VacuumTaskParams vacuumParameters = VacuumTaskParams.create().
            build();
        final VacuumTaskResult result = task.execute( vacuumParameters );

        Assert.assertEquals( 2, result.getProcessed() );
        Assert.assertEquals( 1, result.getDeleted() );
        Assert.assertEquals( 1, result.getInUse() );
        Assert.assertEquals( 1, blobStore.listSegments().count() );
        Assert.assertEquals( segment, blobStore.listSegments().findFirst().get() );
    }
}
