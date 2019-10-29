package com.enonic.xp.repo.impl.vacuum.blob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;


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
