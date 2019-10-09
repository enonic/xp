package com.enonic.xp.repo.impl.vacuum.blob;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.vacuum.VacuumListener;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BinaryBlobVacuumTaskTest extends AbstractBlobVacuumTaskTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.segment = Segment.from( SegmentLevel.from( "test" ), NodeConstants.BINARY_SEGMENT_LEVEL );
    }

    @Test
    public void test_delete_unused()
        throws Exception
    {
        super.test_delete_unused();
    }

    @Test
    public void test_progress_report()
        throws Exception
    {
        super.test_progress_report();
    }

    @Test
    public void age_threshold()
        throws Exception
    {
        super.age_threshold();
    }

    @Override
    protected VacuumTask createTask()
    {
        final BinaryBlobVacuumTask task = new BinaryBlobVacuumTask();
        task.setBlobStore( this.blobStore );
        task.setNodeService( this.nodeService );
        return task;
    }
}
