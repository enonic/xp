package com.enonic.xp.repo.impl.vacuum.segment;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SegmentVacuumTaskTest
{
    private MemoryBlobStore blobStore;

    private RepositoryId repositoryId;

    private RepositoryId repositoryId2;

    private Segment segment;

    private Segment segment2;

    private RepositoryService repositoryService;

    private NodeService nodeService;

    @BeforeEach
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


        this.nodeService = Mockito.mock( NodeService.class );
        Mockito.when( nodeService.findVersions( Mockito.any( NodeVersionQuery.class ) ) ).thenReturn( NodeVersionQueryResult.create().build() );
    }

    @Test
    public void test()
    {
        final BlobRecord record = blobStore.addRecord( segment, ByteSource.wrap( "hello".getBytes() ) );
        final BlobRecord record2 = blobStore.addRecord( segment2, ByteSource.wrap( "hello".getBytes() ) );
        assertEquals( 2, blobStore.listSegments().count() );

        blobStore.removeRecord( segment, record2.getKey() );
        assertEquals( 2, blobStore.listSegments().count() );

        final SegmentVacuumTask task = new SegmentVacuumTask();
        task.setBlobStore( blobStore );
        task.setRepositoryService( repositoryService );
        task.setNodeService( nodeService );

        final VacuumTaskParams vacuumParameters = VacuumTaskParams.create().vacuumStartedAt( Instant.now() ).build();
        final VacuumTaskResult result = task.execute( vacuumParameters );

        assertEquals( 2, result.getProcessed() );
        assertEquals( 1, result.getDeleted() );
        assertEquals( 1, result.getInUse() );
        assertEquals( 1, blobStore.listSegments().count() );
        assertEquals( segment, blobStore.listSegments().findFirst().get() );
    }
}
